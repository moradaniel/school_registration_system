package com.registration.repository;

import com.registration.filter.CourseFilter;
import com.registration.model.Course;
import com.registration.model.CourseId;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface CourseRepositoryCustom {

    PageImpl<Course> findByCriteria(CourseFilter courseQueryFilter, Pageable pageable);

    Optional<Course> loadByCourseId(CourseId courseId);


}
