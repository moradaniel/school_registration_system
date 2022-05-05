package com.registration.repository;

import com.registration.model.Student;
import com.registration.model.StudentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends StudentRepositoryCustom, JpaRepository<Student, Long> {

   Optional<Student> findByStudentId(StudentId studentId);

}