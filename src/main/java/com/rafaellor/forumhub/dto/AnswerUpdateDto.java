package com.rafaellor.forumhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerUpdateDto {

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
