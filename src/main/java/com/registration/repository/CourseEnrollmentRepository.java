package com.registration.repository;

import com.registration.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends CourseEnrollmentRepositoryCustom,JpaRepository<CourseEnrollment,Long>{

    String COURSES_WITHOUT_STUDENTS_QUERY = "SELECT course FROM Course course WHERE course.id not in "
            +"( select courseEnrollment.course.id "
            +" from CourseEnrollment courseEnrollment "
            +" GROUP BY courseEnrollment.course.id "
            +"HAVING COUNT(courseEnrollment.course.id) > 0)";
    @Query(COURSES_WITHOUT_STUDENTS_QUERY)
    List<Course> findCoursesWithoutStudents();


    String STUDENTS_WITHOUT_COURSES_QUERY = "SELECT student FROM Student student WHERE student.id not in "
            +"( select courseEnrollment.student.id "
            +" from CourseEnrollment courseEnrollment "
            +" GROUP BY courseEnrollment.student.id "
            +"HAVING COUNT(courseEnrollment.student.id) > 0)";
    @Query(STUDENTS_WITHOUT_COURSES_QUERY)
    List<Student> findStudentsWithoutCourses();

    Optional<CourseEnrollment> findByCourseAndStudent(Course course, Student student);
}
