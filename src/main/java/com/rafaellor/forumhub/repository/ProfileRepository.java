package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
