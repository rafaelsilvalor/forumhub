package com.rafaellor.forumhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaellor.forumhub.dto.AnswerCreateDto;
import com.rafaellor.forumhub.dto.AnswerUpdateDto;
import com.rafaellor.forumhub.model.Answer;
import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.AnswerRepository;
import com.rafaellor.forumhub.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnswerController.class)
class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TopicRepository topicRepository;

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public AnswerRepository answerRepository() {
            return Mockito.mock(AnswerRepository.class);
        }

        @Bean
        public TopicRepository topicRepository() {
            return Mockito.mock(TopicRepository.class);
        }
    }

    @Test
    @DisplayName("Should return 201 Created when creating a valid answer")
    @WithMockUser(username = "test.user")
    void createAnswer_withValidData_shouldReturn201() throws Exception {
        // Arrange
        AnswerCreateDto createDto = new AnswerCreateDto();
        createDto.setMessage("This is a test answer.");
        createDto.setTopicId(1L);

        User author = new User(1L, "Test User", "test@user.com", "test.user", "password", null);
        Course course = new Course(1L, "Spring Boot", "Backend");
        Topic topic = new Topic(1L, "Test Topic", "Message", LocalDateTime.now(), true, author, course, List.of());

        Answer savedAnswer = new Answer(10L, createDto.getMessage(), topic, LocalDateTime.now(), author, false);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        // Act & Assert
        mockMvc.perform(post("/answers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(savedAnswer.getId()))
                .andExpect(jsonPath("$.message").value(createDto.getMessage()))
                .andExpect(jsonPath("$.authorUsername").value(author.getUsername()));
    }

    @Test
    @DisplayName("Should return 200 OK when updating an answer")
    @WithMockUser(username = "test.user")
    void updateAnswer_withValidData_shouldReturn200() throws Exception {
        // Arrange
        AnswerUpdateDto updateDto = new AnswerUpdateDto();
        updateDto.setMessage("Updated message");

        User author = new User(1L, "Test User", "test@user.com", "test.user", "password", null);
        Topic topic = new Topic();
        Answer existingAnswer = new Answer(10L, "Original message", topic, LocalDateTime.now(), author, false);

        when(answerRepository.findById(10L)).thenReturn(Optional.of(existingAnswer));

        // Act & Assert
        mockMvc.perform(put("/answers/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated message"));
    }
}
