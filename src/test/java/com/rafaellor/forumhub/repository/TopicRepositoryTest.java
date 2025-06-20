package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use real DB if configured, otherwise H2
@ActiveProfiles("test") // Use a dedicated 'test' profile if you have one, or 'dev' for H2
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestEntityManager em; // Used to manage entities in tests

    private Topic createAndPersistTopic(String title, String message, String author, String course) {
        Topic topic = new Topic(title, message, author, course);
        return em.persistAndFlush(topic);
    }

    @Test
    @DisplayName("Should return true when a topic with the given title exists")
    void existsByTitleScenario1() {
        createAndPersistTopic("Unique Title", "Some message", "Author A", "Course X");
        assertTrue(topicRepository.existsByTitle("Unique Title"));
    }

    @Test
    @DisplayName("Should return false when a topic with the given title does not exist")
    void existsByTitleScenario2() {
        assertFalse(topicRepository.existsByTitle("Nonexistent Title"));
    }

    @Test
    @DisplayName("Should return true when a topic with the given message exists")
    void existsByMessageScenario1() {
        createAndPersistTopic("Another Title", "Unique Message", "Author B", "Course Y");
        assertTrue(topicRepository.existsByMessage("Unique Message"));
    }

    @Test
    @DisplayName("Should return false when a topic with the given message does not exist")
    void existsByMessageScenario2() {
        assertFalse(topicRepository.existsByMessage("Nonexistent Message"));
    }

    @Test
    @DisplayName("Should find a topic by ID")
    void findByIdTest() {
        Topic topic = createAndPersistTopic("Find Me", "Message for find", "Author C", "Course Z");
        Topic foundTopic = topicRepository.findById(topic.getId()).orElse(null);
        assertNotNull(foundTopic);
        assertEquals(topic.getTitle(), foundTopic.getTitle());
    }

    @Test
    @DisplayName("Should save a new topic successfully")
    void saveTopicTest() {
        Topic newTopic = new Topic("New Saved Title", "New Saved Message", "New Author", "New Course");
        Topic savedTopic = topicRepository.save(newTopic);

        assertNotNull(savedTopic.getId());
        assertEquals("New Saved Title", savedTopic.getTitle());
        assertTrue(savedTopic.getStatus()); // Default status
        assertNotNull(savedTopic.getCreationDate());
    }

    @Test
    @DisplayName("Should delete a topic by ID")
    void deleteTopicByIdTest() {
        Topic topic = createAndPersistTopic("Delete Me", "Message to delete", "Author D", "Course W");
        Long topicId = topic.getId();

        assertTrue(topicRepository.existsById(topicId));
        topicRepository.deleteById(topicId);
        assertFalse(topicRepository.existsById(topicId));
    }
}
