package com.rafaellor.forumhub.dto;

import com.rafaellor.forumhub.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponseDto {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime creationDate;
    private Boolean status;
    private String authorUsername;
    private String courseName; // Mudar de 'String course' para 'String courseName'

    // Construtor para converter Topic em DTO
    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.message = topic.getMessage();
        this.creationDate = topic.getCreationDate();
        this.status = topic.getStatus();
        this.authorUsername = topic.getAuthor() != null ? topic.getAuthor().getUsername() : null;
        // Acessar o nome do curso a partir do objeto Curso associado
        this.courseName = topic.getCurso() != null ? topic.getCurso().getNome() : null;
    }
}
