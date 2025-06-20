package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // Spring Data JPA automatically provides methods like save, findById, findAll, deleteById, etc.
    // You can add custom query methods here if needed, e.g., findByTitle, findByAuthor, etc.
    boolean existsByTitle(String title);
    boolean existsByMessage(String message);
}
