package com.rafaellor.forumhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.dto.TopicResponseDto;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean; // Added import
import org.springframework.context.annotation.Configuration; // Added import
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // Changed to static import for brevity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class) // Only load TopicController and its dependencies
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc; // For making HTTP requests to the controller

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to/from JSON

    // Removed @MockBean from here.
    private TopicRepository topicRepository;

    // Constructor to inject the mocked repository
    public TopicControllerTest(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    // Static nested @Configuration class to provide the mock bean
    @Configuration
    static class TestConfig {
        @Bean
        public TopicRepository topicRepository() {
            // Create and return the mock using Mockito.mock()
            return mock(TopicRepository.class);
        }
    }

    @Test
    @DisplayName("Should return 201 Created when creating a valid topic")
    void createTopicScenario1() throws Exception {
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("New Test Title");
        createDto.setMessage("New Test Message for controller");
        createDto.setAuthor("Controller Author");
        createDto.setCourse("Controller Course");

        Topic savedTopic = new Topic(1L, createDto.getTitle(), createDto.getMessage(), LocalDateTime.now(), true, createDto.getAuthor(), createDto.getCourse());

        when(topicRepository.existsByTitle(anyString())).thenReturn(false);
        when(topicRepository.existsByMessage(anyString())).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedTopic.getId()))
                .andExpect(jsonPath("$.title").value(createDto.getTitle()));

        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a topic with duplicate title")
    void createTopicScenario2() throws Exception {
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("Existing Title");
        createDto.setMessage("Unique Message");
        createDto.setAuthor("Author");
        createDto.setCourse("Course");

        when(topicRepository.existsByTitle(anyString())).thenReturn(true); // Simulate duplicate title

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title already exists")); // Check the error message in DTO
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating a topic with duplicate message")
    void createTopicScenario3() throws Exception {
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("Unique Title");
        createDto.setMessage("Existing Message");
        createDto.setAuthor("Author");
        createDto.setCourse("Course");

        when(topicRepository.existsByTitle(anyString())).thenReturn(false);
        when(topicRepository.existsByMessage(anyString())).thenReturn(true); // Simulate duplicate message

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message already exists")); // Check the error message in DTO
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating an invalid topic (e.g., blank title)")
    void createTopicScenario4() throws Exception {
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle(""); // Invalid
        createDto.setMessage("Valid Message");
        createDto.setAuthor("Author");
        createDto.setCourse("Course");

        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest()); // Expect bad request due to validation errors
    }

    @Test
    @DisplayName("Should return 200 OK and a list of topics when calling getAllTopics")
    void getAllTopicsScenario() throws Exception {
        Topic topic1 = new Topic(1L, "Title 1", "Message 1", LocalDateTime.now(), true, "Author 1", "Course 1");
        Topic topic2 = new Topic(2L, "Title 2", "Message 2", LocalDateTime.now(), false, "Author 2", "Course 2");
        List<Topic> topics = List.of(topic1, topic2);

        when(topicRepository.findAll()).thenReturn(topics);

        mockMvc.perform(get("/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].author").value("Author 2"));

        verify(topicRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return 200 OK and the topic when calling getTopicById with existing ID")
    void getTopicByIdScenario1() throws Exception {
        Long topicId = 1L;
        Topic topic = new Topic(topicId, "Single Topic", "Details", LocalDateTime.now(), true, "User", "Dev");

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topicId))
                .andExpect(jsonPath("$.title").value("Single Topic"));

        verify(topicRepository, times(1)).findById(topicId);
    }

    @Test
    @DisplayName("Should return 404 Not Found when calling getTopicById with non-existing ID")
    void getTopicByIdScenario2() throws Exception {
        Long topicId = 99L;
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isNotFound());

        verify(topicRepository, times(1)).findById(topicId);
    }

    @Test
    @DisplayName("Should return 200 OK when updating an existing topic")
    void updateTopicScenario1() throws Exception {
        Long topicId = 1L;
        Topic existingTopic = new Topic(topicId, "Original Title", "Original Message", LocalDateTime.now(), true, "Author", "Course");
        TopicCreateDto updateDto = new TopicCreateDto(); // Using TopicCreateDto for updates as per controller
        updateDto.setTitle("Updated Title");
        updateDto.setMessage("Updated Message");
        updateDto.setAuthor("Author"); // Keep author same for simplicity
        updateDto.setCourse("Updated Course");

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
        when(topicRepository.existsByTitle(anyString())).thenReturn(false); // No duplicate title
        when(topicRepository.existsByMessage(anyString())).thenReturn(false); // No duplicate message

        mockMvc.perform(put("/topics/{id}", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.message").value("Updated Message"))
                .andExpect(jsonPath("$.course").value("Updated Course"));

        verify(topicRepository, times(1)).findById(topicId);
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating a non-existing topic")
    void updateTopicScenario2() throws Exception {
        Long topicId = 99L;
        TopicCreateDto updateDto = new TopicCreateDto();
        updateDto.setTitle("New Title");
        updateDto.setMessage("New Message");
        updateDto.setAuthor("Author");
        updateDto.setCourse("Course");

        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/topics/{id}", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(topicRepository, times(1)).findById(topicId);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating topic with duplicate title")
    void updateTopicScenario3() throws Exception {
        Long topicId = 1L;
        Topic existingTopic = new Topic(topicId, "Original Title", "Original Message", LocalDateTime.now(), true, "Author", "Course");
        TopicCreateDto updateDto = new TopicCreateDto();
        updateDto.setTitle("Another Existing Title"); // Attempt to change to an existing title
        updateDto.setMessage("Original Message");
        updateDto.setAuthor("Author");
        updateDto.setCourse("Course");

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(existingTopic));
        when(topicRepository.existsByTitle(updateDto.getTitle())).thenReturn(true); // Simulate duplicate title exists

        mockMvc.perform(put("/topics/{id}", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title already exists"));
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting an existing topic")
    void deleteTopicScenario1() throws Exception {
        Long topicId = 1L;
        when(topicRepository.existsById(topicId)).thenReturn(true);
        doNothing().when(topicRepository).deleteById(topicId); // Mock void method

        mockMvc.perform(delete("/topics/{id}", topicId))
                .andExpect(status().isNoContent());

        verify(topicRepository, times(1)).existsById(topicId);
        verify(topicRepository, times(1)).deleteById(topicId);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting a non-existing topic")
    void deleteTopicScenario2() throws Exception {
        Long topicId = 99L;
        when(topicRepository.existsById(topicId)).thenReturn(false);

        mockMvc.perform(delete("/topics/{id}", topicId))
                .andExpect(status().isNotFound());

        verify(topicRepository, times(1)).existsById(topicId);
        verify(topicRepository, never()).deleteById(anyLong()); // Ensure deleteById is NOT called
    }
}
