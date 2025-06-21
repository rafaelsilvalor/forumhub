package com.rafaellor.forumhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection, which is necessary for stateless APIs
                .csrf(csrf -> csrf.disable())

                // Configure session management to be stateless
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules for HTTP requests
                .authorizeHttpRequests(req -> {
                    // Allow unauthenticated access to the login and register endpoints
                    req.requestMatchers(HttpMethod.POST, "/login", "/register").permitAll();

                    // Allow access to the H2 console for development
                    req.requestMatchers("/h2-console/**").permitAll();

                    // All other requests must be authenticated
                    req.anyRequest().authenticated();
                })

                // For H2 console to work, we need to disable frame options
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // Add our custom JWT filter before the standard username/password filter
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
