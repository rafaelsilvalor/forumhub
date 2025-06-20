package com.rafaellor.forumhub.dto;

import com.rafaellor.forumhub.model.Topic;
import lombok.AllArgsConstructor; // Ensure this is present
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor // Keep this for the full argument constructor for error messages
public class TopicResponseDto {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime creationDate;
    private Boolean status;
    private String authorUsername; // Changed name to reflect User relationship
    private String course;

    // Constructor to convert Topic entity to DTO
    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.message = topic.getMessage();
        this.creationDate = topic.getCreationDate();
        this.status = topic.getStatus();
        // Access username from the associated User object
        this.authorUsername = topic.getAuthor() != null ? topic.getAuthor().getUsername() : null;
        this.course = topic.getCourse();
    }

    // Explicit constructor for all fields (useful for error messages, as previously discussed)
    // Updated to reflect authorUsername instead of author
    // Ensure order matches field order if you relied on Lombok's @AllArgsConstructor implicit order
    // (Long id, String title, String message, LocalDateTime creationDate, Boolean status, String author, String course)
    // becomes:
    // public TopicResponseDto(Long id, String title, String message, LocalDateTime creationDate, Boolean status, String authorUsername, String course) { ... }
}
