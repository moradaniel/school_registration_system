package com.registration.integration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.registration.dto.*;
import com.registration.dto.enrollment.create.CreateCourseEnrollmentRequestDto;
import com.registration.dto.enrollment.create.CreateCourseEnrollmentResponseDto;
import com.registration.model.*;
import com.registration.repository.CourseRepository;
import com.registration.repository.StudentRepository;
import com.registration.util.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CourseEnrollmentsIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;


    private static File COURSES_DATA_JSON = Paths.get("src", "test", "resources", "courses.json").toFile();
    private static File STUDENTS_DATA_JSON = Paths.get("src", "test", "resources", "students.json").toFile();
    private static File ENROLLMENT_DATA_JSON = Paths.get("src", "test", "resources", "updateEnrollmentsRequest.json").toFile();

    Map<CourseId, Course> mapCourse = new HashMap<>();
    Map<StudentId, Student> mapStudent = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();
    JavaType studentApiResponseType;
    JavaType courseApiResponseType;

    JavaType createCourseEnrollmetyResponseApiResponseType;

    JavaType pageStudentDtoApiResponseType;
    JavaType apiResponsePageStudentDtoApiResponseType;


    JavaType pageCourseDtoApiResponseType;
    JavaType apiResponsePageCourseDtoApiResponseType;


    JavaType listCourseDtoApiResponseType;
    JavaType apiResponseListCourseDtoApiResponseType;

    JavaType listStudentDtoApiResponseType;
    JavaType apiResponseListStudentDtoApiResponseType;

    public CourseEnrollmentsIntegrationTest() throws Exception {
        studentApiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, StudentDto.class);
        courseApiResponseType =  mapper.getTypeFactory().constructParametricType(ApiResponse.class, CourseDto.class);
        createCourseEnrollmetyResponseApiResponseType =  mapper.getTypeFactory().constructParametricType(ApiResponse.class, CreateCourseEnrollmentResponseDto.class);


        pageStudentDtoApiResponseType = mapper.getTypeFactory().constructParametricType(RestResponsePage.class, StudentDto.class);
        apiResponsePageStudentDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, pageStudentDtoApiResponseType);

        pageCourseDtoApiResponseType = mapper.getTypeFactory().constructParametricType(RestResponsePage.class, CourseDto.class);
        apiResponsePageCourseDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, pageCourseDtoApiResponseType);

        listCourseDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ArrayList.class, CourseDto.class);
        apiResponseListCourseDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, listCourseDtoApiResponseType);

        listStudentDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ArrayList.class, StudentDto.class);
        apiResponseListStudentDtoApiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, listStudentDtoApiResponseType);
    }

    @BeforeEach
    public void setup() throws Exception {

        cleanTestDatabase();

        CourseDto[] courseDtos = new ObjectMapper().readValue(COURSES_DATA_JSON, CourseDto[].class);


        Arrays.stream(courseDtos)
                .map(CourseDto::of)
                .forEach((course)->{
                    Course savedCourse = courseRepository.save(course);
                    mapCourse.put(savedCourse.getCourseId(),savedCourse);
                });


        StudentDto[] studentDtos = new ObjectMapper().readValue(STUDENTS_DATA_JSON, StudentDto[].class);

        Arrays.stream(studentDtos)
                .map(StudentDto::of)
                .forEach((student)->{
                    Student savedStudent = studentRepository.save(student);
                    mapStudent.put(savedStudent.getStudentId(),savedStudent);
                });



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
    @DisplayName("Positive - Test student found")
    void testFindStudentByStudentId() throws Exception {

        Student mockStudent = mapStudent.get(new StudentId("123e4567-e89b-42d3-a456-556642440000"));

        MvcResult mvcResult = mockMvc.perform(get("/students/{studentId}", mockStudent.getStudentId().asString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<StudentDto> apiResponse = mapper.readValue(json, studentApiResponseType);


        assertThat(apiResponse.getResponse()).usingRecursiveComparison().isEqualTo(StudentDto.of(mockStudent));

    }

    @Test
    @DisplayName("Positive - Add a new Student")
    void testAddNewStudent() throws Exception {

        Student newStudent = new Student("John","Locke", new StudentId(UUID.randomUUID().toString()));

        MvcResult mvcResult =  mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(StudentDto.of(newStudent))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String jsonResponseString = mvcResult.getResponse().getContentAsString();

        ApiResponse<StudentDto> apiResponse = mapper.readValue(jsonResponseString, studentApiResponseType);

        assertThat(apiResponse.getResponse()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(StudentDto.of(newStudent));

    }

    @Test
    @DisplayName("Positive - Add a new Course")
    void testAddNewCourse() throws Exception {

        Course newCourse = new Course("System Design", new CourseId(UUID.randomUUID().toString()));

        MvcResult mvcResult =  mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(CourseDto.of(newCourse))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String jsonResponseString = mvcResult.getResponse().getContentAsString();

        ApiResponse<CourseDto> apiResponse = mapper.readValue(jsonResponseString, courseApiResponseType);

        assertThat(apiResponse.getResponse()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(CourseDto.of(newCourse));

    }


    @Test
    @DisplayName("Positive - Test course found")
    void testFindCourseByCourseId() throws Exception {

        Course mockCourse = mapCourse.get(new CourseId("321e4567-e89b-42d3-a456-556642440000"));

        MvcResult mvcResult = mockMvc.perform(get("/courses/{courseId}", mockCourse.getCourseId().asString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<CourseDto> apiResponse = mapper.readValue(json, courseApiResponseType);


        assertThat(apiResponse.getResponse()).usingRecursiveComparison().isEqualTo(CourseDto.of(mockCourse));

    }





    @Test
    @DisplayName("Positive - Add a new CourseEnrollment")
    void testAddNewCourseEnrollment() throws Exception {

        CreateCourseEnrollmentRequestDto createEnrollmentRequestDto = new CreateCourseEnrollmentRequestDto("321e4567-e89b-42d3-a456-556642440001", "123e4567-e89b-42d3-a456-556642440002");

        CreateCourseEnrollmentResponseDto expectedCreateEnrollmentResponseDto = new CreateCourseEnrollmentResponseDto("321e4567-e89b-42d3-a456-556642440001", "123e4567-e89b-42d3-a456-556642440002");

        MvcResult mvcResult =  mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(createEnrollmentRequestDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String jsonResponseString = mvcResult.getResponse().getContentAsString();

        ApiResponse<CreateCourseEnrollmentResponseDto> apiResponse = mapper.readValue(jsonResponseString, createCourseEnrollmetyResponseApiResponseType);

        assertThat(apiResponse.getResponse()).isEqualTo(expectedCreateEnrollmentResponseDto);

    }


    @Test
    @DisplayName("Positive - Filter all students with a specific course")
    void testFilterAllStudentsWithASpecificCourse() throws Exception {

        Course mockCourse = mapCourse.get(new CourseId("321e4567-e89b-42d3-a456-556642440000"));

        MvcResult mvcResult = mockMvc.perform(get("/enrollments/course/{courseId}", mockCourse.getCourseId().asString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<Page<StudentDto>> apiResponse = mapper.readValue(json, apiResponsePageStudentDtoApiResponseType);


        assertThat(apiResponse.getResponse().getTotalElements()).isEqualTo(2);

    }


    @Test
    @DisplayName("Positive - Filter all courses for a specific student")
    void testFilterAllCoursesForASpecificStudent() throws Exception {


        Student mockStudent = mapStudent.get(new StudentId("123e4567-e89b-42d3-a456-556642440001"));

        MvcResult mvcResult = mockMvc.perform(get("/enrollments/student/{studentId}", mockStudent.getStudentId().asString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<Page<CourseDto>> apiResponse = mapper.readValue(json, apiResponsePageCourseDtoApiResponseType);


        assertThat(apiResponse.getResponse().getTotalElements()).isEqualTo(1);

    }


    @Test
    @DisplayName("Positive - Filter all courses without any students")
    void testFilterAllCoursesWithoutAnyStudents() throws Exception {


        MvcResult mvcResult = mockMvc.perform(get("/enrollments/courses/withoutStudents"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<List<CourseDto>> apiResponse = mapper.readValue(json, apiResponseListCourseDtoApiResponseType);


        assertThat(apiResponse.getResponse()).hasSize(1);

    }

    @Test
    @DisplayName("Positive - Filter all students without any courses")
    void testFilterAllStudentsWithoutAnyCourses() throws Exception {


        MvcResult mvcResult = mockMvc.perform(get("/enrollments/students/withoutCourses"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<List<StudentDto>> apiResponse = mapper.readValue(json, apiResponseListStudentDtoApiResponseType);


        assertThat(apiResponse.getResponse()).hasSize(1);

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
