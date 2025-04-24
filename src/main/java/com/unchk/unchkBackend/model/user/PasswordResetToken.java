package com.unchk.unchkBackend.model.user;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Date expirationDate;

    @OneToOne
    private User user;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Date expirationDate, User user) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.user = user;
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
