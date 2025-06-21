package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByName(String name);
}
