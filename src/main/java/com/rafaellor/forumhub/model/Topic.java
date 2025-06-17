package com.rafaellor.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;
    private LocalDateTime creationDate;
    private Boolean status; // true for active, false for closed

    // Relationships (simplified for now, assuming String for author/course names)
    private String author; // Placeholder for User entity
    private String course; // Placeholder for Course entity

    // Constructor for creating new topics (without ID, creationDate, and default status)
    public Topic(String title, String message, String author, String course) {
        this.title = title;
        this.message = message;
        this.creationDate = LocalDateTime.now();
        this.status = true; // Default to active
        this.author = author;
        this.course = course;
    }

    // Method to update topic
    public void update(String title, String message, String course) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (message != null && !message.isBlank()) {
            this.message = message;
        }
        if (course != null && !course.isBlank()) {
            this.course = course;
        }
    }

    // Method to close topic
    public void close() {
        this.status = false;
    }
}
