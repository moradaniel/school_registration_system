package com.registration.service;

import com.registration.dto.CourseDto;
import com.registration.dto.StudentDto;
import com.registration.filter.CourseEnrollmentFilter;
import com.registration.repository.CourseEnrollmentRepository;
import com.registration.repository.CourseRepository;
import com.registration.repository.StudentRepository;
import com.registration.util.Result;
import com.registration.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CourseEnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseEnrollmentService.class);
    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public CourseEnrollment save(CourseEnrollment courseEnrollment){
        LOGGER.info("Saving new CourseEnrollment: {}", courseEnrollment);
        return courseEnrollmentRepository.save(courseEnrollment);
    }




    public Iterable<CourseEnrollment> findAll() {
        return courseEnrollmentRepository.findAll();
    }



    @Transactional
    public Page<StudentDto> findStudentsByCriteria(CourseEnrollmentFilter courseEnrollmentFilter, PageRequest pageRequest) {

        LOGGER.info("attempting to find students by criteria");

        Page<CourseEnrollment> foundCourseEnrollments = courseEnrollmentRepository.findByCriteria(courseEnrollmentFilter, pageRequest);

        List<StudentDto> foundStudentsDTOs = new ArrayList<>();

        for(CourseEnrollment courseEnrollment:foundCourseEnrollments){
            foundStudentsDTOs.add(StudentDto.of(courseEnrollment.getStudent()));
        }

        return new PageImpl<>(foundStudentsDTOs, pageRequest, foundCourseEnrollments.getTotalElements());

    }

    @Transactional
    public Page<CourseDto> findCoursesByCriteria(CourseEnrollmentFilter courseEnrollmentFilter, PageRequest pageRequest) {

        LOGGER.info("attempting to find courses by criteria");

        Page<CourseEnrollment> foundCourseEnrollments = courseEnrollmentRepository.findByCriteria(courseEnrollmentFilter, pageRequest);

        List<CourseDto> foundCourseDtos = new ArrayList<>();

        for(CourseEnrollment courseEnrollment:foundCourseEnrollments){

            foundCourseDtos.add(CourseDto.of(courseEnrollment.getCourse()));
        }

        return new PageImpl<>(foundCourseDtos, pageRequest, foundCourseEnrollments.getTotalElements());


    }

    @Transactional
    public List<CourseDto> findCoursesWithoutStudents() {

        LOGGER.info("attempting to findCoursesWithoutStudents");


        List<Course> foundCourses = courseEnrollmentRepository.findCoursesWithoutStudents();

        List<CourseDto> foundCourseDtos = new ArrayList<>();

        for(Course course:foundCourses){

            foundCourseDtos.add(CourseDto.of(course));
        }

        return foundCourseDtos;

    }

    @Transactional
    public List<StudentDto> findStudentsWithoutCourses() {

        LOGGER.info("attempting to findStudentsWithoutCourses");


        List<Student> foundStudents = courseEnrollmentRepository.findStudentsWithoutCourses();

        List<StudentDto> foundStudentDtos = new ArrayList<>();

        for(Student student:foundStudents){

            foundStudentDtos.add(StudentDto.of(student));
        }

        return foundStudentDtos;

    }


    @Transactional
    public Result<CourseEnrollment> create(final CourseId courseId, StudentId studentId){

        Result<CourseEnrollment> result;

        List<String> errors = new ArrayList<>();

        Optional<Course> optionalCourse = courseRepository.findByCourseId(courseId);
        if(!optionalCourse.isPresent()){
            errors.add("course does not exist");
            return new Result<>(null, errors);
        }

        Optional<Student> optionalStudent = studentRepository.findByStudentId(studentId);
        if(!optionalStudent.isPresent()){
            errors.add("student does not exist");
            return new Result<>(null, errors);
        }

        int pageNumber = 0;
        int pageSize = 2000;

        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize);

        CourseEnrollmentFilter courseEnrollmentFilter = new CourseEnrollmentFilter();
        courseEnrollmentFilter.setCourseCourseIds(Arrays.asList(courseId));

        Page<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findByCriteria(courseEnrollmentFilter, pageRequest);
        if(courseEnrollments.getTotalElements() >= 50){
            errors.add("A course has 50 students maximum");
            return new Result<>(null, errors);
        }


        courseEnrollmentFilter = new CourseEnrollmentFilter();
        courseEnrollmentFilter.setStudentStudentIds(Arrays.asList(studentId));

        Page<CourseEnrollment> studentEnrollments = courseEnrollmentRepository.findByCriteria(courseEnrollmentFilter, pageRequest);
        if(studentEnrollments.getTotalElements() >= 5){
            errors.add("A student can register to 5 course maximum");
            return new Result<>(null, errors);
        }

        Student student = optionalStudent.get();
        CourseEnrollment courseEnrollment = new CourseEnrollment(student, optionalCourse.get());
        student.addCourseEnrollment(courseEnrollment);

        studentRepository.save(student);


        result = new Result<>(courseEnrollment);
        return result;
    }

}
