package com.rafaellor.forumhub.dto;

import com.rafaellor.forumhub.model.Topic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicResponseDto {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime creationDate;
    private Boolean status;
    private String author;
    private String course;

    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.message = topic.getMessage();
        this.creationDate = topic.getCreationDate();
        this.status = topic.getStatus();
        this.author = topic.getAuthor();
        this.course = topic.getCourse();
    }
}
