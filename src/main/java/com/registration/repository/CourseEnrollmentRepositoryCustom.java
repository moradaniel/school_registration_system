package com.registration.repository;

import com.registration.filter.CourseEnrollmentFilter;
import com.registration.model.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CourseEnrollmentRepositoryCustom {

    Page<CourseEnrollment> findByCriteria(CourseEnrollmentFilter courseEnrollment, Pageable pageable);

}
