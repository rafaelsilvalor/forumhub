package com.rafaellor.forumhub.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    private User author;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create mock/stub objects for dependencies for each test
        author = new User(1L, "Test Author", "author@test.com", "testauthor", "password", null);
        course = new Course(1L, "Test Course", "Testing");
    }

    @Test
    @DisplayName("Should create a topic with default status true and current creation date")
    void constructor_shouldSetDefaultsCorrectly() {
        // Arrange
        String title = "Test Title";
        String message = "Test Message";

        // Act
        Topic topic = new Topic(title, message, author, course);

        // Assert
        assertNull(topic.getId(), "ID should be null before persistence");
        assertEquals(title, topic.getTitle());
        assertEquals(message, topic.getMessage());
        assertEquals(author, topic.getAuthor(), "Should correctly assign the author object");
        assertEquals(course, topic.getCourse(), "Should correctly assign the course object");
        assertTrue(topic.getStatus(), "New topic status should default to true (active)");
        assertNotNull(topic.getCreationDate(), "Creation date should not be null");
        assertTrue(topic.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)), "Creation date should be current");
    }

    @Test
    @DisplayName("Should update topic title, message, and course correctly")
    void update_shouldModifyFields() {
        // Arrange
        Topic topic = new Topic("Old Title", "Old Message", author, course);
        String newTitle = "New Title";
        String newMessage = "New Message";
        Course newCourse = new Course(2L, "New Course", "New Category");

        // Act
        topic.update(newTitle, newMessage, newCourse);

        // Assert
        assertEquals(newTitle, topic.getTitle());
        assertEquals(newMessage, topic.getMessage());
        assertEquals(newCourse, topic.getCourse(), "The course should be updated to the new object");
    }

    @Test
    @DisplayName("Should not update topic fields if new values are null or blank")
    void update_withNullOrBlank_shouldNotModifyFields() {
        // Arrange
        String originalTitle = "Original Title";
        String originalMessage = "Original Message";
        Topic topic = new Topic(originalTitle, originalMessage, author, course);
        Course originalCourse = topic.getCourse();

        // Act: Attempt to update with null title, blank message, and null course
        topic.update(null, " ", null);

        // Assert
        assertEquals(originalTitle, topic.getTitle(), "Title should not change if the new value is null");
        assertEquals(originalMessage, topic.getMessage(), "Message should not change if the new value is blank");
        assertEquals(originalCourse, topic.getCourse(), "Course should not change if the new value is null");
    }

    @Test
    @DisplayName("Should set topic status to false when closed")
    void close_shouldSetStatusToFalse() {
        // Arrange
        Topic topic = new Topic("An Open Topic", "Message", author, course);
        assertTrue(topic.getStatus(), "Topic should be active initially");

        // Act
        topic.close();

        // Assert
        assertFalse(topic.getStatus(), "Topic status should be false after closing");
    }
}
