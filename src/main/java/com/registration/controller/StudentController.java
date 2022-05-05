package com.registration.controller;

import com.registration.dto.ApiResponse;
import com.registration.dto.StudentCreateDtoValidator;
import com.registration.dto.StudentDto;
import com.registration.model.Student;
import com.registration.model.StudentId;
import com.registration.service.StudentService;
import com.registration.util.Result;
import com.registration.util.UUIDValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Tag(name="StudentController", description = "REST API related to Students")
@RestController
@RequestMapping(value = "/students")
public class StudentController {

    private static final Logger LOGGER = LogManager.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    //------------ validation ------------------------------------

    @Autowired
    private StudentCreateDtoValidator studentCreateDtoValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null
                && StudentDto.class.equals(binder.getTarget().getClass())) {
            binder.setValidator(studentCreateDtoValidator);
        }
    }
    //------------------------------------------------------------------------------


    @GetMapping("/{studentId}")
    @Operation(summary = "Returns a specific student by their identifier.")

    public ResponseEntity<ApiResponse<StudentDto>> getStudent(
            @Parameter(description="studentId of the student to be obtained. Cannot be empty.",
                    required=true)
            @PathVariable String studentId){


        if(!UUIDValidator.isValidUUID(studentId)){
            throw new IllegalArgumentException("studentId must be a valid UUID");
        }

        Optional<Student> studentOptional = studentService.findByStudentId(new StudentId(studentId));
        if (studentOptional.isPresent()){

            ApiResponse<StudentDto> apiResponse = new ApiResponse<>(StudentDto.of(studentOptional.get()));

                return ResponseEntity
                        .ok()
                        .body(apiResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Add Student.")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(
            @Parameter(description="The StudentDto request. Cannot be empty.",
                    required=true, content = @Content(schema = @Schema(implementation = StudentDto.class)))
            @Valid @RequestBody StudentDto request) {
        LOGGER.info("Attempting to save student {}", request.getStudentId());

        Result<StudentDto> result = studentService.save(request);

        if(!result.isOk()){
            return ResponseEntity.badRequest().body(new ApiResponse<>(result.getErrors()));
        }

        try {
            return ResponseEntity
                    .created(new URI("/students/"+result.getTarget().getStudentId()))
                    .body(new ApiResponse<>(result.getTarget()));
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
