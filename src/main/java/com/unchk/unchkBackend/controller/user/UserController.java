package com.unchk.unchkBackend.controller.user;

import com.unchk.unchkBackend.model.user.User;
import com.unchk.unchkBackend.dto.user.UpdateUserRequest;
import com.unchk.unchkBackend.dto.user.StudentCreationRequest;
import com.unchk.unchkBackend.dto.user.UpdateStudentRequest;
import com.unchk.unchkBackend.dto.user.UserResponse;
import com.unchk.unchkBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.unchk.unchkBackend.service.user.AuthService;

import java.util.List;
import com.unchk.unchkBackend.model.user.Role;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole()
        );
    }


    @PutMapping("/me")
    public String updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody UpdateUserRequest request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Met à jour les champs autorisés
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return "Informations mises à jour avec succès !";
    }


    @PostMapping("/create-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createStudent(@RequestBody StudentCreationRequest request) {
        return authService.createStudent(request);
    }


    @PutMapping("/students/{id}")
        @PreAuthorize("hasRole('ADMIN')")

public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody UpdateStudentRequest request) {
    authService.updateStudent(id, request);
    return ResponseEntity.ok("Étudiant mis à jour avec succès.");
}

@DeleteMapping("/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")

public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
    authService.deleteStudent(id);
    return ResponseEntity.ok("Étudiant supprimé avec succès.");
}

@GetMapping("/students")
@PreAuthorize("hasRole('ADMIN')")

public List<User> getAllStudents() {
    return userRepository.findAll()
        .stream()
        .filter(user -> user.getRole() == Role.STUDENT)
        .collect(Collectors.toList());
}


@GetMapping("/students/{id}")
@PreAuthorize("hasRole('ADMIN')")

public User getStudentById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));
    if (user.getRole() != Role.STUDENT) {
        throw new RuntimeException("Cet utilisateur n'est pas un étudiant");
    }
    return user;
}


// public ResponseEntity<UserResponse> getStudentById(@PathVariable Long id) {
//     User student = authService.getStudentById(id);
//     UserResponse response = new UserResponse(
//             student.getId(),
//             student.getFirstName(),
//             student.getLastName(),
//             student.getEmail(),
//             student.getRole()
//     );
//     return ResponseEntity.ok(response);
// }

}