package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Security will use this method to find a user by username
    User findByUsername(String username);
}
