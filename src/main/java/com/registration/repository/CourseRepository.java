package com.registration.repository;

import com.registration.model.Course;
import com.registration.model.CourseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends CourseRepositoryCustom, JpaRepository<Course, Long> {

   Optional<Course> findByCourseId(CourseId courseId);

}