package com.rafaellor.forumhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.model.Answer;
import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.CourseRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public TopicRepository topicRepository() {
            return Mockito.mock(TopicRepository.class);
        }

        @Bean
        public CourseRepository courseRepository() {
            return Mockito.mock(CourseRepository.class);
        }
    }

    @Test
    @DisplayName("Should return 201 Created when creating a valid topic")
    @WithMockUser(username = "test.user")
    void createTopic_withValidData_shouldReturn201() throws Exception {
        // Arrange
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("Valid Title");
        createDto.setMessage("A valid message for the topic.");
        createDto.setCourseId(1L);

        User author = new User(1L, "test.user", "password");
        Course course = new Course(1L, "Spring Boot", "Backend");
        Topic savedTopic = new Topic("Valid Title", "A valid message for the topic.", author, course);
        savedTopic.setId(10L);

        when(topicRepository.existsByTitle(anyString())).thenReturn(false);
        when(topicRepository.existsByMessage(anyString())).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        // Act & Assert
        mockMvc.perform(post("/topics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedTopic.getId()))
                .andExpect(jsonPath("$.title").value(createDto.getTitle()))
                .andExpect(jsonPath("$.authorUsername").value(author.getUsername()))
                .andExpect(jsonPath("$.courseName").value(course.getName()));
    }

    @Test
    @DisplayName("Should return 409 Conflict when creating a topic with a duplicate title")
    @WithMockUser(username = "test.user")
    void createTopic_withDuplicateTitle_shouldReturn409() throws Exception {
        // Arrange
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("Duplicate Title");
        createDto.setMessage("Some message.");
        createDto.setCourseId(1L);

        when(topicRepository.existsByTitle("Duplicate Title")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/topics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 200 OK with a list of all topics")
    @WithMockUser
    void getAllTopics_shouldReturnTopicList() throws Exception {
        // Arrange
        User author = new User(1L, "author", "pass");
        Course course = new Course(1L, "Java", "Programming");
        Topic topic = new Topic(10L, "Topic Title", "Topic Message", LocalDateTime.now(), true, author, course, null);

        when(topicRepository.findAll()).thenReturn(List.of(topic));

        // Act & Assert
        mockMvc.perform(get("/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Topic Title"));
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting an existing topic")
    @WithMockUser
    void deleteTopic_withExistingId_shouldReturn204() throws Exception {
        // Arrange
        Long existingId = 1L;
        when(topicRepository.existsById(existingId)).thenReturn(true);
        doNothing().when(topicRepository).deleteById(existingId);

        // Act & Assert
        mockMvc.perform(delete("/topics/{id}", existingId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(topicRepository, times(1)).existsById(existingId);
        verify(topicRepository, times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Should return 200 OK and a list of answers for a given topic")
    @WithMockUser
    void getAnswersForTopic_withValidTopicId_shouldReturnAnswersList() throws Exception {
        // Arrange
        Long topicId = 1L;
        User topicAuthor = new User(1L, "topic.author", "pass");
        User answerAuthor = new User(2L, "answer.author", "pass");
        Course course = new Course(1L, "Testing", "QA");
        Topic topic = new Topic(topicId, "Topic with answers", "message", LocalDateTime.now(), true, topicAuthor, course, null);

        Answer answer1 = new Answer(10L, "First answer", topic, LocalDateTime.now().minusHours(1), answerAuthor, false);
        Answer answer2 = new Answer(11L, "Second answer", topic, LocalDateTime.now(), answerAuthor, false);
        topic.setAnswers(List.of(answer1, answer2));

        when(topicRepository.existsById(topicId)).thenReturn(true);
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        // Act & Assert
        mockMvc.perform(get("/topics/{topicId}/answers", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].message").value("First answer"))
                .andExpect(jsonPath("$[1].message").value("Second answer"))
                .andExpect(jsonPath("$[0].authorUsername").value("answer.author"));
    }
}
