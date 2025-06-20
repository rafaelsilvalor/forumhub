package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.dto.TopicResponseDto;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User; // Import the User entity
import com.rafaellor.forumhub.repository.TopicRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<TopicResponseDto> createTopic(@RequestBody @Valid TopicCreateDto topicCreateDto,
                                                        UriComponentsBuilder uriComponentsBuilder) {

        // Get the authenticated user from the SecurityContextHolder
        // The principal is typically your UserDetails object (which is your User entity)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        // Check for duplicate title or message
        // The author check is implicitly handled by requiring authentication.
        if (topicRepository.existsByTitle(topicCreateDto.getTitle())) {
            // Note: Updated TopicResponseDto constructor to accept String message for errors
            return ResponseEntity.badRequest().body(new TopicResponseDto(null, "Title already exists", null, null, null, null, null));
        }
        if (topicRepository.existsByMessage(topicCreateDto.getMessage())) {
            return ResponseEntity.badRequest().body(new TopicResponseDto(null, null, "Message already exists", null, null, null, null));
        }

        // Create the Topic, passing the authenticated User object as the author
        Topic topic = new Topic(topicCreateDto.getTitle(),
                topicCreateDto.getMessage(),
                authenticatedUser, // Use the authenticated user
                topicCreateDto.getCourse());
        topic = topicRepository.save(topic); // Save and get the managed entity with ID

        URI uri = uriComponentsBuilder.path("/topics/{id}").buildAndExpand(topic.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicResponseDto(topic));
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDto>> getAllTopics() {
        List<TopicResponseDto> topics = topicRepository.findAll().stream()
                .map(TopicResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable Long id) {
        return topicRepository.findById(id)
                .map(topic -> ResponseEntity.ok(new TopicResponseDto(topic)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicResponseDto> updateTopic(@PathVariable Long id,
                                                        @RequestBody @Valid TopicCreateDto topicUpdateDto) { // Use TopicCreateDto as update DTO for simplicity
        return topicRepository.findById(id)
                .map(topic -> {
                    // Check for duplicate title or message if they are being updated to existing ones
                    if (!topic.getTitle().equals(topicUpdateDto.getTitle()) && topicRepository.existsByTitle(topicUpdateDto.getTitle())) {
                        return ResponseEntity.badRequest().body(new TopicResponseDto(null, "Title already exists", null, null, null, null, null));
                    }
                    if (!topic.getMessage().equals(topicUpdateDto.getMessage()) && topicRepository.existsByMessage(topicUpdateDto.getMessage())) {
                        return ResponseEntity.badRequest().body(new TopicResponseDto(null, null, "Message already exists", null, null, null, null));
                    }

                    topic.update(topicUpdateDto.getTitle(), topicUpdateDto.getMessage(), topicUpdateDto.getCourse());
                    return ResponseEntity.ok(new TopicResponseDto(topic));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (topicRepository.existsById(id)) {
            // Optional: Add authorization check here to ensure only the author or admin can delete
            // User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // if (!topic.getAuthor().getId().equals(authenticatedUser.getId())) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            // }
            topicRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
