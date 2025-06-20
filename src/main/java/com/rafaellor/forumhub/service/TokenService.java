package com.rafaellor.forumhub.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rafaellor.forumhub.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Secret key for JWT signing, loaded from application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    // Method to generate a JWT token
    public String generateToken(User user) {
        try {
            // Use HMAC256 algorithm with the secret key
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Build the JWT token
            return JWT.create()
                    .withIssuer("API Forum Hub") // Issuer of the token
                    .withSubject(user.getUsername()) // Subject of the token (username)
                    .withExpiresAt(expirationDate()) // Token expiration time
                    .sign(algorithm); // Sign the token with the algorithm and secret
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    // Method to validate a JWT token and return its subject (username)
    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API Forum Hub")
                    .build()
                    .verify(token) // Verify the token
                    .getSubject(); // Get the subject (username)
        } catch (JWTVerificationException exception){
            // If token is invalid or expired
            throw new RuntimeException("Invalid or expired JWT token", exception);
        }
    }

    // Helper method to calculate token expiration date (2 hours from now)
    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // Adjust to your timezone (e.g., -03:00 for Brazil)
    }
}
