package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
