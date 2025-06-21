package com.rafaellor.forumhub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false) // Change "user_id" to "author_id"
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();


    public Topic(String title, String message, User author, Course course) {
        this.title = title;
        this.message = message;
        this.creationDate = LocalDateTime.now();
        this.status = true; // Default to active
        this.author = author;
        this.course = course;
    }

    public void update(String title, String message, Course course) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (message != null && !message.isBlank()) {
            this.message = message;
        }
        if (course != null) {
            this.course = course;
        }
    }

    public void close() {
        this.status = false;
    }
}
