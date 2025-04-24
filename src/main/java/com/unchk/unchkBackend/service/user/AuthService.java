package com.unchk.unchkBackend.service.user;


import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email déjà utilisé";
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Rôle invalide (utilisez ADMIN ou STUDENT)";
        }

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        userRepository.save(user);
        return "Utilisateur inscrit avec succès !";
    }


    public LoginResponse login(LoginRequest request) {
        // Authentifie avec Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si auth réussie → récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer le JWT
        String token = jwtService.generateToken(user.getEmail());
        
UserResponse userResponse = new UserResponse(user); // <-- tu crées un UserResponse à partir du User
return new LoginResponse(token, userResponse);
        // return new LoginResponse(token);
    }


    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur avec cet email"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1h

        tokenRepository.save(resetToken);

        emailService.sendResetPasswordEmail(email, token);
        return "Un lien de réinitialisation a été envoyé à votre adresse email.";
    }


    public String resetPassword(String token, ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return "Les mots de passe ne correspondent pas.";
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.getExpirationDate().before(new Date())) {
            return "Le lien de réinitialisation a expiré.";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        return "Mot de passe réinitialisé avec succès.";
    }


    public String createStudent(StudentCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email déjà utilisé";
        }
    
        // Générer un mot de passe aléatoire
        String generatedPassword = generateRandomPassword(9);
    
        User student = new User(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            passwordEncoder.encode(generatedPassword),
            Role.STUDENT
        );
    
        userRepository.save(student);
    
        // Envoyer un mail avec les identifiants
        emailService.sendStudentCredentialsEmail(
            request.getEmail(),
            generatedPassword
        );
    
        return "Étudiant créé et e-mail envoyé";
    }


    public void updateStudent(Long studentId, UpdateStudentRequest request) {
    Optional<User> optionalUser = userRepository.findById(studentId);
    if (optionalUser.isEmpty()) {
        throw new RuntimeException("Étudiant non trouvé.");
    }

    User student = optionalUser.get();
    student.setFirstName(request.getFirstName());
    student.setLastName(request.getLastName());
    userRepository.save(student);
}

public void deleteStudent(Long studentId) {
    if (!userRepository.existsById(studentId)) {
        throw new RuntimeException("Étudiant non trouvé.");
    }

    userRepository.deleteById(studentId);
}


public List<User> getAllStudents() {
    return userRepository.findByRole("STUDENT");
}

public User getStudentById(Long id) {
    return userRepository.findById(id)
            .filter(user -> user.getRole().equals("STUDENT"))
            .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
}

    
}
