package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitle(String title);
    boolean existsByMessage(String message);
}
