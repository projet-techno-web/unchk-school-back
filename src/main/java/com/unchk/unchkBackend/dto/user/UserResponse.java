package com.unchk.unchkBackend.dto.user;

import com.unchk.unchkBackend.model.user.Role;
import com.unchk.unchkBackend.model.user.User;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    // ✅ Constructeur utilisé manuellement
    public UserResponse(Long id, String firstName, String lastName, String email, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    // ✅ Constructeur pratique depuis un objet User
    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
