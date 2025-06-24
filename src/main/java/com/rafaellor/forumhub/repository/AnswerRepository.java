package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByTopicId(Long topicId, Pageable pageable);
}
