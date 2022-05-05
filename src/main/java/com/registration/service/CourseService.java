package com.registration.service;

import com.registration.dto.CourseDto;
import com.registration.dto.StudentDto;
import com.registration.model.Course;
import com.registration.model.CourseId;
import com.registration.model.Student;
import com.registration.model.StudentId;
import com.registration.repository.CourseRepository;
import com.registration.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public Result<CourseDto> save(CourseDto courseDto){
        LOGGER.info("Saving Course: {}", courseDto);
        Optional<Course> existingCourse = courseRepository.loadByCourseId(new CourseId(courseDto.getCourseId()));
        if(existingCourse.isPresent()){
           return update(courseDto);
        }
        Course savedCourse = courseRepository.save(CourseDto.of(courseDto));

        return new Result<>(CourseDto.of(savedCourse));
    }


    @Transactional
    public Result<CourseDto> update(CourseDto courseDto){
        LOGGER.info("Updating Course with couseId:{}", courseDto.getCourseId());
        Optional<Course> existingCourseOptional = courseRepository.findByCourseId(new CourseId(courseDto.getCourseId()));
        Course existingCourse = null;
        if (existingCourseOptional.isPresent()){
            existingCourse = existingCourseOptional.get();
            Course updatedCourse = new Course(existingCourse.getId(),
                    courseDto.getName(),
                    new CourseId(courseDto.getCourseId()));

            existingCourse = courseRepository.save(updatedCourse);
        } else {
            LOGGER.error("Course with CourseId {} could not be updated!", courseDto.getCourseId());
            throw new EntityNotFoundException("Not found Course " + courseDto.getCourseId());
        }
        return new Result<>(CourseDto.of(existingCourse));
    }

    @Transactional
    public Optional<Course> findById(Long id){
        LOGGER.info("Finding Course by id:{}", id);
        return courseRepository.findById(id);
    }

    @Transactional
    public Optional<Course> findByCourseId(CourseId courseId){
        LOGGER.info("Finding Course by CourseId:{}", courseId);
        return courseRepository.findByCourseId(courseId);
    }

    @Transactional
    public void delete(Long id){
        LOGGER.info("Deleting student with id:{}", id);
        Optional<Course> existingCourse = courseRepository.findById(id);
        if (existingCourse.isPresent()){
            courseRepository.deleteById(existingCourse.get().getId());
        } else {
            LOGGER.error("Course with id {} could not be found!", id);
            throw new EntityNotFoundException("Not found Course " + id);
        }
    }

    public Iterable<Course> findAll() {
        return courseRepository.findAll();
    }


}
