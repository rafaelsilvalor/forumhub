package com.rafaellor.forumhub.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    @DisplayName("Should create a topic with default status true and current creation date")
    void constructorTest() {
        String title = "Test Title";
        String message = "Test Message";
        String author = "Test Author";
        String course = "Test Course";

        Topic topic = new Topic(title, message, author, course);

        assertNotNull(topic.getId()); // ID should be null until persisted, but for constructor test, we might mock/assume
        // Note: In real JPA, ID is generated on save, so this check might be removed for a pure unit test of constructor logic.
        // For now, it's just a general check on the object's state.
        assertEquals(title, topic.getTitle());
        assertEquals(message, topic.getMessage());
        assertEquals(author, topic.getAuthor());
        assertEquals(course, topic.getCourse());
        assertTrue(topic.getStatus(), "New topic status should be true (active)");
        assertNotNull(topic.getCreationDate(), "Creation date should not be null");
        assertTrue(topic.getCreationDate().isBefore(LocalDateTime.now().plusSeconds(1)), "Creation date should be current");
    }

    @Test
    @DisplayName("Should update topic title, message, and course correctly")
    void updateTopicTest() {
        Topic topic = new Topic("Old Title", "Old Message", "Old Author", "Old Course");

        String newTitle = "New Title";
        String newMessage = "New Message";
        String newCourse = "New Course";

        topic.update(newTitle, newMessage, newCourse);

        assertEquals(newTitle, topic.getTitle());
        assertEquals(newMessage, topic.getMessage());
        assertEquals(newCourse, topic.getCourse());
    }

    @Test
    @DisplayName("Should not update topic fields if new values are null or blank")
    void updateTopicWithNullOrBlankTest() {
        Topic topic = new Topic("Original Title", "Original Message", "Original Author", "Original Course");

        String originalTitle = topic.getTitle();
        String originalMessage = topic.getMessage();
        String originalCourse = topic.getCourse();

        topic.update(null, "", null); // Attempt to update with null and blank

        assertEquals(originalTitle, topic.getTitle());
        assertEquals(originalMessage, topic.getMessage());
        assertEquals(originalCourse, topic.getCourse());
    }


    @Test
    @DisplayName("Should set topic status to false when closed")
    void closeTopicTest() {
        Topic topic = new Topic("Title", "Message", "Author", "Course");
        topic.close();
        assertFalse(topic.getStatus(), "Topic status should be false after closing");
    }
}
