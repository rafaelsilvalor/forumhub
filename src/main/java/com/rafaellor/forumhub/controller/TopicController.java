package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.AnswerResponseDto;
import com.rafaellor.forumhub.dto.TopicCreateDto;
import com.rafaellor.forumhub.dto.TopicResponseDto;
import com.rafaellor.forumhub.model.Answer;
import com.rafaellor.forumhub.model.Course;
import com.rafaellor.forumhub.model.Topic;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.repository.AnswerRepository;
import com.rafaellor.forumhub.repository.CourseRepository;
import com.rafaellor.forumhub.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.net.URI;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<TopicResponseDto> createTopic(@RequestBody @Valid TopicCreateDto topicCreateDto,
                                                        UriComponentsBuilder uriComponentsBuilder) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        if (topicRepository.existsByTitle(topicCreateDto.getTitle())) {
            throw new DataIntegrityViolationException("Title already exists");
        }
        if (topicRepository.existsByMessage(topicCreateDto.getMessage())) {
            throw new DataIntegrityViolationException("Message already exists");
        }

        Course course = courseRepository.findById(topicCreateDto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + topicCreateDto.getCourseId()));

        Topic topic = new Topic(
                topicCreateDto.getTitle(),
                topicCreateDto.getMessage(),
                authenticatedUser, course
        );
        topic = topicRepository.save(topic);

        URI uri = uriComponentsBuilder.path("/topics/{id}").buildAndExpand(topic.getId()).toUri();

        return ResponseEntity.created(uri).body(new TopicResponseDto(topic));
    }

    @GetMapping
    public ResponseEntity<Page<TopicResponseDto>> getAllTopics(
            @PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Topic> topicPage = topicRepository.findAll(pageable);
        Page<TopicResponseDto> topicResponseDtoPage = topicPage.map(TopicResponseDto::new);
        return ResponseEntity.ok(topicResponseDtoPage);
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
                                                        @RequestBody @Valid TopicCreateDto topicUpdateDto) {
        return topicRepository.findById(id)
                .map(topic -> {
                    if (!topic.getTitle().equals(topicUpdateDto.getTitle()) && topicRepository.existsByTitle(topicUpdateDto.getTitle())) {
                        throw new DataIntegrityViolationException("Title already exists");
                    }
                    if (!topic.getMessage().equals(topicUpdateDto.getMessage()) && topicRepository.existsByMessage(topicUpdateDto.getMessage())) {
                        throw new DataIntegrityViolationException("Message already exists");
                    }

                    Course course = courseRepository.findById(topicUpdateDto.getCourseId())
                            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + topicUpdateDto.getCourseId()));

                    topic.update(topicUpdateDto.getTitle(), topicUpdateDto.getMessage(), course);

                    return ResponseEntity.ok(new TopicResponseDto(topic));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (topicRepository.existsById(id)) {
            topicRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/{topicId}/answers")
    public ResponseEntity<Page<AnswerResponseDto>> getAnswersForTopic(@PathVariable Long topicId,
                                                                      @PageableDefault(size = 20, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {

        if (!topicRepository.existsById(topicId)) {
            return ResponseEntity.notFound().build();
        }

        Page<Answer> answerPage = answerRepository.findByTopicId(topicId, pageable);

        return ResponseEntity.ok(answerPage.map(AnswerResponseDto::new));
    }

}
