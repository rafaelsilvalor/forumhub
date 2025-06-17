package com.rafaellor.forumhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicCreateDto {
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Message cannot be blank")
    @Size(min = 10, max = 500, message = "Message must be between 10 and 500 characters")
    private String message;

    @NotBlank(message = "Author cannot be blank")
    private String author;

    @NotBlank(message = "Course cannot be blank")
    private String course;
}
