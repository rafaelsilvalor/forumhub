package com.rafaellor.forumhub.dto;

import com.rafaellor.forumhub.model.Answer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AnswerResponseDto {

    private Long id;
    private String message;
    private LocalDateTime creationDate;
    private String authorUsername;
    private Boolean solution;

    // Constructor to map from the Answer entity
    public AnswerResponseDto(Answer answer) {
        this.id = answer.getId();
        this.message = answer.getMessage();
        this.creationDate = answer.getCreationDate();
        this.solution = answer.getSolution();
        if (answer.getAuthor() != null) {
            this.authorUsername = answer.getAuthor().getUsername();
        }
    }
}
