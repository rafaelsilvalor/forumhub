package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.UserCreateDto;
import com.rafaellor.forumhub.model.Profile;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.ProfileRepository;
import com.rafaellor.forumhub.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/register")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> registerUser(@RequestBody @Valid UserCreateDto createDto) {
        if (userRepository.findByUsername(createDto.getUsername()) != null) {
            throw new DataIntegrityViolationException("Username already exists.");
        }
        if (userRepository.findByEmail(createDto.getEmail()) != null) {
            throw new DataIntegrityViolationException("Email already in use.");
        }

        User newUser = new User();
        newUser.setName(createDto.getName());
        newUser.setUsername(createDto.getUsername());
        newUser.setEmail(createDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(createDto.getPassword()));

        Profile defaultProfile = profileRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default profile ROLE_USER not found."));
        newUser.setProfiles(Set.of(defaultProfile));

        userRepository.save(newUser);

        return ResponseEntity.created(null).build(); // Return 201 Created with no body
    }
}
