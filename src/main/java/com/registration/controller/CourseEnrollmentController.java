package com.registration.controller;

import com.registration.dto.ApiResponse;
import com.registration.dto.CourseDto;
import com.registration.dto.StudentCreateDtoValidator;
import com.registration.dto.StudentDto;
import com.registration.dto.enrollment.create.CreateCourseEnrollmentRequestDto;
import com.registration.dto.enrollment.create.CreateCourseEnrollmentResponseDto;
import com.registration.filter.CourseEnrollmentFilter;
import com.registration.model.CourseEnrollment;
import com.registration.model.CourseId;
import com.registration.model.StudentId;
import com.registration.service.CourseEnrollmentService;
import com.registration.service.StudentService;
import com.registration.util.Result;
import com.registration.util.UUIDValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Tag(name="CourseEnrollmentController", description = "REST API related to CourseEnrollment")
@RestController
@RequestMapping(value = "/enrollments")
public class CourseEnrollmentController {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

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


    @GetMapping("/course/{courseId}")
    @Operation(summary = "Returns students by course.")
    public  ResponseEntity<ApiResponse<Page<StudentDto>>> findStudentsByCourse(
            @Parameter(description="courseId of the course. Cannot be empty.",
                    required=true)
            @PathVariable String courseId  ){

        if(!UUIDValidator.isValidUUID(courseId)){
            throw new IllegalArgumentException("courseId must be a valid UUID");
        }

        ApiResponse<Page<StudentDto>> apiResponse = new ApiResponse<>();

        //TODO make this configurable
        int pageNumber = 0;
        int pageSize = 2000;

        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize);

        CourseEnrollmentFilter courseEnrollmentFilter = new CourseEnrollmentFilter();
        courseEnrollmentFilter.setCourseCourseIds(Arrays.asList(new CourseId(courseId)));

        Page<StudentDto> result = courseEnrollmentService.findStudentsByCriteria(courseEnrollmentFilter, pageRequest);

        apiResponse.setResponse(result);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/student/{studentId}")
    @Operation(summary = "Returns courses by student.")
    public  ResponseEntity<ApiResponse<Page<CourseDto>>> findCourseEnrollmentsByStudent(
            @Parameter(description="studentId of the student. Cannot be empty.",
                    required=true)
            @PathVariable String studentId  ){

        if(!UUIDValidator.isValidUUID(studentId)){
            throw new IllegalArgumentException("studentId must be a valid UUID");
        }

        ApiResponse<Page<CourseDto>> apiResponse = new ApiResponse<>();

        //TODO make this configurable
        int pageNumber = 0;
        int pageSize = 2000;

        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize);

        CourseEnrollmentFilter courseEnrollmentFilter = new CourseEnrollmentFilter();
        courseEnrollmentFilter.setStudentStudentIds(Arrays.asList(new StudentId(studentId)));

        Page<CourseDto> result = courseEnrollmentService.findCoursesByCriteria(courseEnrollmentFilter, pageRequest);

        apiResponse.setResponse(result);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/courses/withoutStudents")
    @Operation(summary = "Returns courses with no student.")
    public  ResponseEntity<ApiResponse<List<CourseDto>>> findCoursesWithoutStudents(){

        ApiResponse<List<CourseDto>> apiResponse = new ApiResponse<>();

        List<CourseDto> result = courseEnrollmentService.findCoursesWithoutStudents();

        apiResponse.setResponse(result);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/students/withoutCourses")
    @Operation(summary = "Returns students with no courses.")
    public  ResponseEntity<ApiResponse<List<StudentDto>>> findStudentsWithoutCourses(){

        ApiResponse<List<StudentDto>> apiResponse = new ApiResponse<>();

        List<StudentDto> result = courseEnrollmentService.findStudentsWithoutCourses();

        apiResponse.setResponse(result);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @Operation(summary = "Create enrollment.")
    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse<CreateCourseEnrollmentResponseDto>> createEnrollment(
            @Parameter(description="The CreateEnrollmentRequestDto request. Cannot be empty.",
                    required=true, content = @Content(schema = @Schema(implementation = CreateCourseEnrollmentRequestDto.class)))
            @Valid @RequestBody CreateCourseEnrollmentRequestDto request) {

        Result<CourseEnrollment> result = courseEnrollmentService.create(new CourseId(request.getCourseId()) , new StudentId(request.getStudentId()));


        if(!result.isOk()){

            return ResponseEntity.badRequest().body(new ApiResponse<>(result.getErrors()));
        }

        ApiResponse<CreateCourseEnrollmentResponseDto> apiResponse = new ApiResponse<>(CreateCourseEnrollmentResponseDto.of(result.getTarget().getCourse(), result.getTarget().getStudent()));

        try {
            return ResponseEntity
                    .created(new URI("/enrollments/"))
                    .body(apiResponse);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
