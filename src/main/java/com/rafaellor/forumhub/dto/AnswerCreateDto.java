package com.rafaellor.forumhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerCreateDto {

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Topic ID cannot be null")
    private Long topicId;
}
