package com.registration.repository;

import com.registration.filter.StudentFilter;
import com.registration.model.Student;
import com.registration.model.StudentId;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface StudentRepositoryCustom {

    PageImpl<Student> findByCriteria(StudentFilter studentQueryFilter, Pageable pageable);

    Optional<Student> loadByStudentId(StudentId studentId);

}
