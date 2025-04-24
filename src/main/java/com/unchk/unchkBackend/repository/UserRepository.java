package com.unchk.unchkBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unchk.unchkBackend.model.user.User;

import java.util.Optional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(String role); // par exemple : "STUDENT"
}