package com.registration.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registration.dto.CourseDto;
import com.registration.dto.EnrolledStudentDto;
import com.registration.dto.StudentDto;
import com.registration.dto.UpdateCourseEnrollmentDto;
import com.registration.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;


@ExtendWith({SpringExtension.class})
@SpringBootTest
class EnrollmentRepositoryTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;


    private static File COURSES_DATA_JSON = Paths.get("src", "test", "resources", "courses.json").toFile();
    private static File STUDENTS_DATA_JSON = Paths.get("src", "test", "resources", "students.json").toFile();
    private static File ENROLLMENT_DATA_JSON = Paths.get("src", "test", "resources", "updateEnrollmentsRequest.json").toFile();

    @BeforeEach
    public void setup() throws Exception {
        cleanTestDatabase();

        CourseDto[] courseDtos = new ObjectMapper().readValue(COURSES_DATA_JSON, CourseDto[].class);

        Arrays.stream(courseDtos)
                .map(CourseDto::of)
                .forEach(courseRepository::save);


        StudentDto[] studentDtos = new ObjectMapper().readValue(STUDENTS_DATA_JSON, StudentDto[].class);

        Arrays.stream(studentDtos)
                .map(StudentDto::of)
                .forEach(studentRepository::save);




        UpdateCourseEnrollmentDto[] updateCourseEnrollmentDtos = new ObjectMapper().readValue(ENROLLMENT_DATA_JSON, UpdateCourseEnrollmentDto[].class);

        for(UpdateCourseEnrollmentDto updateCourseEnrollmentDto: updateCourseEnrollmentDtos) {
            Optional<Course> course = courseRepository.findByCourseId(new CourseId(updateCourseEnrollmentDto.getCourseID().toString()));

            for (EnrolledStudentDto enrolledStudentDto : updateCourseEnrollmentDto.getEnrolledStudents()) {

                Optional<Student> student = studentRepository.loadByStudentId(new StudentId(enrolledStudentDto.getStudentID().toString()));
                student.get().addCourseEnrollment(new CourseEnrollment(student.get(),course.get()));

                studentRepository.save(student.get());
            }
        }

    }

    @AfterEach
    public void cleanup(){
    }


    @Test
    @DisplayName("Test Enrollment deleted successfully")
    void testEnrollmentDeletedSuccessfully(){
        Optional<Course> course = courseRepository.findByCourseId(new CourseId("321e4567-e89b-42d3-a456-556642440000"));
        Optional<Student> student = studentRepository.findByStudentId(new StudentId("123e4567-e89b-42d3-a456-556642440000"));

        Optional<CourseEnrollment> courseEnrollment = courseEnrollmentRepository.findByCourseAndStudent(course.get(),student.get());

        courseEnrollmentRepository.deleteById(courseEnrollment.get().getId());

        courseEnrollment = courseEnrollmentRepository.findByCourseAndStudent(course.get(),student.get());

        Assertions.assertFalse(courseEnrollment.isPresent());
    }

    private void cleanTestDatabase() throws Exception{
        Connection dbConnection = null;
        try{
            dbConnection = dataSource.getConnection();
            //clean test database before each test
            ScriptUtils.executeSqlScript(dbConnection,
                    new ClassPathResource("sql/clean_db_schema.sql"));

        } catch (SQLException e ) {
            throw new RuntimeException(e);
        } finally {
            try { if (dbConnection != null) dbConnection.close(); } catch (Exception e) {};
        }
    }
}
