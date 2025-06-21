package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.AnswerCreateDto;
import com.rafaellor.forumhub.dto.AnswerResponseDto;
import com.rafaellor.forumhub.model.Answer;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.AnswerRepository;
import com.rafaellor.forumhub.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.rafaellor.forumhub.dto.AnswerUpdateDto;
import org.springframework.http.HttpStatus;

import java.net.URI;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TopicRepository topicRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<AnswerResponseDto> createAnswer(@RequestBody @Valid AnswerCreateDto createDto, UriComponentsBuilder uriBuilder) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = (User) authentication.getPrincipal();

        // Find the topic to which the answer belongs
        Topic topic = topicRepository.findById(createDto.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with id: " + createDto.getTopicId()));

        // Create and save the new answer
        Answer answer = new Answer(createDto.getMessage(), topic, author);
        answerRepository.save(answer);

        // Return a 201 Created response
        URI uri = uriBuilder.path("/answers/{id}").buildAndExpand(answer.getId()).toUri();
        return ResponseEntity.created(uri).body(new AnswerResponseDto(answer));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

        // Optional: Add authorization logic here. For example, only the author or an admin can delete.
        // User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // if (!answer.getAuthor().equals(authenticatedUser)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }

        answerRepository.delete(answer);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<AnswerResponseDto> updateAnswer(@PathVariable Long id, @RequestBody @Valid AnswerUpdateDto updateDto) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

        // Authorization Check: Ensure the person updating is the original author
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!answer.getAuthor().equals(authenticatedUser)) {
            // In a real app, you'd throw an AccessDeniedException handled by your ErrorHandler
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        answer.setMessage(updateDto.getMessage());
        // The transaction will commit the change to the database

        return ResponseEntity.ok(new AnswerResponseDto(answer));
    }

    @PatchMapping("/{id}/solution")
    @Transactional
    public ResponseEntity<Void> markAsSolution(@PathVariable Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

        Topic topic = answer.getTopic();

        // Authorization Check: Ensure the person marking the solution is the author of the TOPIC
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!topic.getAuthor().equals(authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Mark this answer as the solution
        answer.setSolution(true);

        // Optionally, close the topic since it's now solved
        topic.close();

        return ResponseEntity.noContent().build();
    }
}
