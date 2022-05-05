package com.registration.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registration.dto.StudentDto;
import com.registration.exception.EntityNotFoundException;
import com.registration.model.Student;
import com.registration.model.StudentId;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class StudentRepositoryTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    private StudentRepository studentRepository;

    private static File DATA_JSON = Paths.get("src", "test", "resources", "students.json").toFile();

    @BeforeEach
    public void setup() throws Exception {
        cleanTestDatabase();

        StudentDto[] studentDtos = new ObjectMapper().readValue(DATA_JSON, StudentDto[].class);

        Arrays.stream(studentDtos)
                .map(StudentDto::of)
                .forEach(studentRepository::save);
    }

    @AfterEach
    public void cleanup(){
    }

    @Test
    @DisplayName("Test student not found with non-existing id")
    void testStudentNotFoundForNonExistingId(){

        // When
        Optional<Student> retrievedStudent = studentRepository.findByStudentId(new StudentId(UUID.randomUUID().toString()));

        // Then
        Assertions.assertFalse(retrievedStudent.isPresent(), "Student should not exist");
    }


    @Test
    @DisplayName("Test student saved successfully")
    void testStudentSavedSuccessfully(){

        Student newStudent = new Student("John","Locke", new StudentId(UUID.randomUUID().toString()));

        studentRepository.save(newStudent);

        Optional<Student> savedStudent = studentRepository.findByStudentId(newStudent.getStudentId());

        assertTrue(savedStudent.isPresent(), "Student should be saved");
        assertNotNull(savedStudent.get().getId(), "Student should have an id when saved");

    }


    @Test
    @DisplayName("Test student updated successfully")
    void testStudentUpdatedSuccessfully(){

        StudentId studentId = new StudentId("123e4567-e89b-42d3-a456-556642440000");

        Student existingStudent = studentRepository.loadByStudentId(studentId).orElseThrow(() -> new EntityNotFoundException("Not found student with id "+studentId));

        existingStudent.setLastName("Cranson");

        studentRepository.save(existingStudent);

        Optional<Student> updatedStudent = studentRepository.findById(existingStudent.getId());

        assertThat(updatedStudent.get()).isEqualToIgnoringGivenFields(existingStudent, "courseEnrollments" );
    }

    @Test
    @DisplayName("Test student deleted successfully")
    void testStudentDeletedSuccessfully(){

        Optional<Student> student = studentRepository.findByStudentId(new StudentId("123e4567-e89b-42d3-a456-556642440002"));

        studentRepository.deleteById(student.get().getId());

        student = studentRepository.findByStudentId(student.get().getStudentId());

        Assertions.assertFalse(student.isPresent());

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
