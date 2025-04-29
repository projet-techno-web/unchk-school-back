package com.unchk.unchkBackend.service.user;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unchk.unchkBackend.dto.user.LoginRequest;
import com.unchk.unchkBackend.dto.user.LoginResponse;
import com.unchk.unchkBackend.dto.user.RegisterRequest;
import com.unchk.unchkBackend.dto.user.ResetPasswordRequest;
import com.unchk.unchkBackend.dto.user.StudentCreationRequest;
import com.unchk.unchkBackend.dto.user.UpdateStudentRequest;
import com.unchk.unchkBackend.dto.user.UserResponse;
import com.unchk.unchkBackend.model.user.PasswordResetToken;
import com.unchk.unchkBackend.model.user.Role;
import com.unchk.unchkBackend.model.user.User;
import com.unchk.unchkBackend.repository.PasswordResetTokenRepository;
import com.unchk.unchkBackend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé"));
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (utilisez ADMIN ou STUDENT)"));
        }

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Utilisateur inscrit avec succès !"));
    }

    public ResponseEntity<?> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(new LoginResponse(token, new UserResponse(user)));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }

    public ResponseEntity<?> forgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aucun utilisateur avec cet email"));
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 heure

        tokenRepository.save(resetToken);
        emailService.sendResetPasswordEmail(email, token);

        return ResponseEntity.ok(Map.of("message", "Un lien de réinitialisation a été envoyé à votre adresse email."));
    }

    public ResponseEntity<?> resetPassword(String token, ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Les mots de passe ne correspondent pas."));
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.getExpirationDate().before(new Date())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le lien de réinitialisation a expiré."));
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès."));
    }

    public ResponseEntity<?> createStudent(StudentCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé"));
        }

        String generatedPassword = generateRandomPassword(9);

        User student = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(generatedPassword),
                Role.STUDENT
        );

        userRepository.save(student);

        emailService.sendStudentCredentialsEmail(request.getEmail(), generatedPassword);

        return ResponseEntity.ok(Map.of("message", "Étudiant créé et e-mail envoyé"));
    }

    public ResponseEntity<?> updateStudent(Long studentId, UpdateStudentRequest request) {
        Optional<User> optionalUser = userRepository.findById(studentId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Étudiant non trouvé."));
        }

        User student = optionalUser.get();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        userRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Étudiant mis à jour avec succès."));
    }

    public ResponseEntity<?> deleteStudent(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Étudiant non trouvé."));
        }

        userRepository.deleteById(studentId);
        return ResponseEntity.ok(Map.of("message", "Étudiant supprimé avec succès."));
    }

    public List<User> getAllStudents() {
        return userRepository.findByRole("STUDENT");
    }

    public ResponseEntity<?> getStudentById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() || optionalUser.get().getRole() != Role.STUDENT) {
            return ResponseEntity.badRequest().body(Map.of("message", "Étudiant non trouvé"));
        }

        return ResponseEntity.ok(new UserResponse(optionalUser.get()));
    }
}
