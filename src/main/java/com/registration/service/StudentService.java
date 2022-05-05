package com.registration.service;

import com.registration.dto.StudentDto;
import com.registration.model.Student;
import com.registration.model.StudentId;
import com.registration.repository.StudentRepository;
import com.registration.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);
    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public Result<StudentDto> save(StudentDto studentDto){
        LOGGER.info("Saving Student: {}", studentDto);
        Optional<Student> existingStudent = studentRepository.loadByStudentId(new StudentId(studentDto.getStudentId().toString()));
        if(existingStudent.isPresent()){
           return update(studentDto);
        }
        Student savedStudent = studentRepository.save(StudentDto.of(studentDto));

        return new Result<>(StudentDto.of(savedStudent));
    }

    @Transactional
    public Result<StudentDto> update(StudentDto studentDto){
        LOGGER.info("Updating student with studentId:{}", studentDto.getStudentId().toString());
        Optional<Student> existingStudentOptional = studentRepository.findByStudentId(new StudentId(studentDto.getStudentId().toString()));
        Student existingStudent = null;
        if (existingStudentOptional.isPresent()){
            existingStudent = existingStudentOptional.get();
            Student updatedStudent = new Student(existingStudent.getId(),
                    studentDto.getFirstName(),
                    studentDto.getLastName(),
                    new StudentId(studentDto.getStudentId().toString()));

            existingStudent = studentRepository.save(updatedStudent);
        } else {
            //TODO Entity not found exception
            LOGGER.error("Student with studentId {} could not be updated!", studentDto.getStudentId());
            throw new EntityNotFoundException("Not found student " + studentDto.getStudentId());
        }
        return new Result<>(StudentDto.of(existingStudent));
    }

    public Optional<Student> findById(Long id){
        LOGGER.info("Finding student by id:{}", id);
        return studentRepository.findById(id);
    }

    @Transactional
    public Optional<Student> findByStudentId(StudentId studentId){
        LOGGER.info("Finding student by studentId:{}", studentId);
        return studentRepository.findByStudentId(studentId);
    }

    @Transactional
    public void delete(Long id){
        LOGGER.info("Deleting student with id:{}", id);
        Optional<Student> existingStudent = studentRepository.findById(id);
        if (existingStudent.isPresent()){
            studentRepository.deleteById(existingStudent.get().getId());
        } else {
            LOGGER.error("Student with id {} could not be found!", id);
            throw new EntityNotFoundException("Not found student " + id);
        }
    }

    @Transactional
    public Iterable<Student> findAll() {
        return studentRepository.findAll();
    }

}
