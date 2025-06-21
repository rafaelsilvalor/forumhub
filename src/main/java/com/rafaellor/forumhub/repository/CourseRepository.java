package com.rafaellor.forumhub.repository;

import com.rafaellor.forumhub.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
