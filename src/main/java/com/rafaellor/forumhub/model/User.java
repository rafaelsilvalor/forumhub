package com.rafaellor.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users") // Renamed from 'user' to 'users' for common convention
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // Implement UserDetails for Spring Security

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Username must be unique
    private String username;

    private String password;

    // You might add roles here later, e.g., private UserRole role;

    // --- UserDetails interface methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For simplicity, we'll return a default role for now.
        // In a real application, you'd load roles from the database.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Always true for now
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Always true for now
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Always true for now
    }

    @Override
    public boolean isEnabled() {
        return true; // Always true for now
    }
}
