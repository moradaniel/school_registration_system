package com.registration.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.registration.dto.ApiResponse;
import com.registration.dto.StudentCreateDtoValidator;
import com.registration.dto.StudentDto;
import com.registration.model.Student;
import com.registration.model.StudentId;
import com.registration.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
@Import({StudentCreateDtoValidator.class})

class StudentControllerTest {

    @MockBean
    private StudentService studentService;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();
    JavaType apiResponseType;

    public StudentControllerTest() throws Exception {
        apiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, StudentDto.class);
    }


    @Test
    @DisplayName("Positive - Test Student found")
    void testFindStudentById() throws Exception {

        Student mockStudent = new Student(1l,"John", "Friedman", new StudentId(UUID.randomUUID().toString()));

        StudentDto expectedStudentDto = StudentDto.of(mockStudent);

        doReturn(Optional.of(mockStudent)).when(studentService).findByStudentId(any(StudentId.class));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/{studentId}", mockStudent.getStudentId().asString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<StudentDto> apiResponse = mapper.readValue(json, apiResponseType);

        assertThat(apiResponse.getResponse()).isEqualToComparingFieldByField(expectedStudentDto);

    }


}
