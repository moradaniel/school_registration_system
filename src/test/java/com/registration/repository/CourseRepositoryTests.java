package com.registration.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.registration.dto.CourseDto;
import com.registration.exception.EntityNotFoundException;
import com.registration.model.Course;
import com.registration.model.CourseId;
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
class CourseRepositoryTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    private CourseRepository courseRepository;

    private static File DATA_JSON = Paths.get("src", "test", "resources", "courses.json").toFile();

    @BeforeEach
    public void setup() throws Exception {
        cleanTestDatabase();

        CourseDto[] courseDtos = new ObjectMapper().readValue(DATA_JSON, CourseDto[].class);

        Arrays.stream(courseDtos)
                .map(CourseDto::of)
                .forEach(courseRepository::save);
    }

    @AfterEach
    public void cleanup(){
    }

    @Test
    @DisplayName("Test Course not found with non-existing id")
    void testCourseNotFoundForNonExistingId(){

        Optional<Course> retrievedCourse = courseRepository.findByCourseId(new CourseId(UUID.randomUUID().toString()));
        Assertions.assertFalse(retrievedCourse.isPresent(), "Course should not exist");
    }


    @Test
    @DisplayName("Test Course saved successfully")
    void testCourseSavedSuccessfully(){

        Course newCourse = new Course("Security", new CourseId(UUID.randomUUID().toString()));

        // When
        courseRepository.save(newCourse);

        Optional<Course> savedCourse = courseRepository.findByCourseId(newCourse.getCourseId());

        // Then
        assertTrue(savedCourse.isPresent(), "Course should be saved");
        assertNotNull(savedCourse.get().getId(), "Course should have an id when saved");

    }


    @Test
    @DisplayName("Test Course updated successfully")
    void testCourseUpdatedSuccessfully(){


        CourseId courseId = new CourseId("321e4567-e89b-42d3-a456-556642440001");

        Course existingCourse = courseRepository.loadByCourseId(courseId).orElseThrow(() -> new EntityNotFoundException("Not found Course with id "+courseId));

        existingCourse.setName("Architecture 2");

        courseRepository.save(existingCourse);

        Optional<Course> updatedCourse = courseRepository.findById(existingCourse.getId());


        assertThat(updatedCourse.get()).isEqualToIgnoringGivenFields(existingCourse, "courseEnrollments" );
    }


    @Test
    @DisplayName("Test Course deleted successfully")
    void testCourseDeletedSuccessfully(){
        Optional<Course> course = courseRepository.findByCourseId(new CourseId("321e4567-e89b-42d3-a456-556642440002"));

        courseRepository.deleteById(course.get().getId());

        course = courseRepository.findByCourseId(course.get().getCourseId());

        Assertions.assertFalse(course.isPresent());
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
