// src/main/java/com/rafaellor/forumhub/model/Topic.java
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

    // Changed from String to ManyToOne relationship with User
    @ManyToOne(fetch = FetchType.LAZY) // Many topics to one user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column name, not nullable
    private User author;

    private String course; // Keeping course as String for now

    // Constructor for creating new topics (without ID, creationDate, and default status)
    // Modified to accept User object instead of String author
    public Topic(String title, String message, User author, String course) {
        this.title = title;
        this.message = message;
        this.creationDate = LocalDateTime.now();
        this.status = true; // Default to active
        this.author = author;
        this.course = course;
    }

    // Constructor needed for JPA/Lombok after adding @AllArgsConstructor for full fields including ID
    // If you explicitly define @AllArgsConstructor, Lombok might conflict, so be mindful.
    // Ensure you have constructors that JPA needs.

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
