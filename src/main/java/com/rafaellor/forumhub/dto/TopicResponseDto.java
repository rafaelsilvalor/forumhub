// src/main/java/com/rafaellor/forumhub/dto/TopicResponseDto.java
package com.rafaellor.forumhub.dto;

import com.rafaellor.forumhub.model.Topic;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TopicResponseDto {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime creationDate;
    private Boolean status;
    private String author;
    private String course;

    // Constructor to convert Topic entity to DTO
    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.message = topic.getMessage();
        this.creationDate = topic.getCreationDate();
        this.status = topic.getStatus();
        this.author = topic.getAuthor();
        this.course = topic.getCourse();
    }

    // Explicit constructor for all fields, to be used for error messages or direct construction
    public TopicResponseDto(Long id, String title, String message, LocalDateTime creationDate, Boolean status, String author, String course) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.creationDate = creationDate;
        this.status = status;
        this.author = author;
        this.course = course;
    }
}
