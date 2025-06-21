package com.rafaellor.forumhub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.CursoRepository;
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

    // Os mocks agora são injetados a partir da nossa classe de configuração de teste
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Classe de configuração interna para fornecer os beans mockados
     * para o contexto de teste do Spring. Esta é a alternativa moderna ao @MockBean.
     */
    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        public TopicRepository topicRepository() {
            return Mockito.mock(TopicRepository.class);
        }

        @Bean
        public CursoRepository cursoRepository() {
            return Mockito.mock(CursoRepository.class);
        }
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao criar um tópico válido")
    @WithMockUser(username = "testuser")
    void createTopicScenario1() throws Exception {
        // Preparação
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("New Test Title");
        createDto.setMessage("New Test Message for controller");
        createDto.setCourseId(1L);

        User mockUser = new User(1L, "testuser", "password");
        Course mockCourse = new Course(1L, "Spring Boot", "Backend");
        Topic savedTopic = new Topic("New Test Title", "New Test Message for controller", mockUser, mockCourse);
        savedTopic.setId(10L);

        // Configurar mocks (agora usando os beans injetados)
        when(topicRepository.existsByTitle(anyString())).thenReturn(false);
        when(topicRepository.existsByMessage(anyString())).thenReturn(false);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        // Ação e Verificação
        mockMvc.perform(post("/topics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(savedTopic.getId()))
                .andExpect(jsonPath("$.title").value(createDto.getTitle()))
                .andExpect(jsonPath("$.authorUsername").value(mockUser.getUsername()))
                .andExpect(jsonPath("$.courseName").value(mockCourse.getName()));

        verify(cursoRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    // --- O restante dos métodos de teste continua igual ---

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar criar um tópico com título duplicado")
    @WithMockUser(username = "testuser")
    void createTopicScenario2() throws Exception {
        TopicCreateDto createDto = new TopicCreateDto();
        createDto.setTitle("Existing Title");
        createDto.setMessage("Unique Message");
        createDto.setCourseId(1L);

        when(topicRepository.existsByTitle("Existing Title")).thenReturn(true);

        mockMvc.perform(post("/topics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Title already exists"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK com a lista de tópicos")
    @WithMockUser
    void getAllTopicsScenario() throws Exception {
        User mockAuthor = new User(1L, "author1", "pass");
        Course mockCourse = new Course(1L, "Java", "Programming");
        Topic topic1 = new Topic(10L, "Title 1", "Message 1", LocalDateTime.now(), true, mockAuthor, mockCourse);

        when(topicRepository.findAll()).thenReturn(List.of(topic1));

        mockMvc.perform(get("/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].authorUsername").value("author1"))
                .andExpect(jsonPath("$[0].courseName").value("Java"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o tópico ao buscar por ID existente")
    @WithMockUser
    void getTopicByIdScenario1() throws Exception {
        Long topicId = 1L;
        User mockAuthor = new User(10L, "singleuser", "pass");
        Course mockCourse = new Course(5L, "DevOps", "CI/CD");
        Topic topic = new Topic(topicId, "Single Topic", "Details", LocalDateTime.now(), true, mockAuthor, mockCourse);

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topicId))
                .andExpect(jsonPath("$.title").value("Single Topic"))
                .andExpect(jsonPath("$.authorUsername").value("singleuser"))
                .andExpect(jsonPath("$.courseName").value("DevOps"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar por ID inexistente")
    @WithMockUser
    void getTopicByIdScenario2() throws Exception {
        Long topicId = 99L;
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar um tópico existente")
    @WithMockUser
    void deleteTopicScenario1() throws Exception {
        Long topicId = 1L;
        when(topicRepository.existsById(topicId)).thenReturn(true);
        doNothing().when(topicRepository).deleteById(topicId);

        mockMvc.perform(delete("/topics/{id}", topicId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(topicRepository, times(1)).existsById(topicId);
        verify(topicRepository, times(1)).deleteById(topicId);
    }
}
