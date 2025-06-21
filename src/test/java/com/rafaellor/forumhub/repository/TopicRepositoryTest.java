package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev") // Use the H2 database profile for tests
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestEntityManager em; // Helper for persisting entities in tests

    @Test
    @DisplayName("Should return true when a topic with the given title exists")
    void existsByTitle_shouldReturnTrue_whenTitleExists() {
        // Arrange: Set up the prerequisite entities
        User author = new User(null, "Test Author", "author@test.com", "testauthor", "password", null);
        em.persist(author);
        Course course = new Course(null, "Spring Boot", "Backend");
        em.persist(course);
        Topic topic = new Topic("Unique Title", "Some message", author, course);
        em.persist(topic);

        // Act
        boolean exists = topicRepository.existsByTitle("Unique Title");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when a topic with the given title does not exist")
    void existsByTitle_shouldReturnFalse_whenTitleDoesNotExist() {
        // Act
        boolean exists = topicRepository.existsByTitle("Nonexistent Title");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when a topic with the given message exists")
    void existsByMessage_shouldReturnTrue_whenMessageExists() {
        // Arrange
        User author = new User(null, "Another Author", "another@test.com", "anotherauthor", "password", null);
        em.persist(author);
        Course course = new Course(null, "Hibernate", "Persistence");
        em.persist(course);
        Topic topic = new Topic("Another Title", "Unique Message", author, course);
        em.persist(topic);

        // Act
        boolean exists = topicRepository.existsByMessage("Unique Message");

        // Assert
        assertThat(exists).isTrue();
    }
}
