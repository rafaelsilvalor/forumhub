package com.rafaellor.forumhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaellor.forumhub.dto.UserCreateDto;
import com.rafaellor.forumhub.model.Profile;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.ProfileRepository;
import com.rafaellor.forumhub.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // The controller's dependencies are mocked via the TestConfiguration below
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public ProfileRepository profileRepository() {
            return Mockito.mock(ProfileRepository.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return Mockito.mock(PasswordEncoder.class);
        }
    }

    @Test
    @DisplayName("Should return 201 Created for a valid user registration")
    void registerUser_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Test User");
        createDto.setUsername("testuser");
        createDto.setEmail("test@example.com");
        createDto.setPassword("password123");

        // Mock repository checks to show user does not exist
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Mock the password encoder
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");

        // Mock finding the default profile
        Profile defaultProfile = new Profile(1L, "ROLE_USER");
        when(profileRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultProfile));

        // Mock the save operation
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf()) // Add CSRF token for tests with Spring Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 409 Conflict if username already exists")
    void registerUser_withDuplicateUsername_shouldReturnConflict() throws Exception {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Another User");
        createDto.setUsername("existinguser");
        createDto.setEmail("another@example.com");
        createDto.setPassword("password123");

        // Mock the repository to find an existing user by username
        when(userRepository.findByUsername("existinguser")).thenReturn(new User());

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 409 Conflict if email is already in use")
    void registerUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
        // Arrange
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Email User");
        createDto.setUsername("newuser");
        createDto.setEmail("existing@example.com");
        createDto.setPassword("password123");

        // Mock username check as passing, but email check as failing
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail("existing@example.com")).thenReturn(new User());

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid registration data")
    void registerUser_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Arrange: DTO with a blank password, which violates @NotBlank
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Invalid User");
        createDto.setUsername("invaliduser");
        createDto.setEmail("invalid@example.com");
        createDto.setPassword(""); // Invalid data

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }
}
