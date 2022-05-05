package com.registration.controller;

import com.registration.dto.ApiResponse;
import com.registration.dto.CourseCreateDtoValidator;
import com.registration.dto.CourseDto;
import com.registration.model.Course;
import com.registration.model.CourseId;
import com.registration.service.CourseService;
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

@Tag(name="CourseController", description = "REST API related to Courses")
@RestController
@RequestMapping(value = "/courses")
public class CourseController {

    private static final Logger LOGGER = LogManager.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    //------------ validation ------------------------------------

    @Autowired
    private CourseCreateDtoValidator courseCreateDtoValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null
                && CourseDto.class.equals(binder.getTarget().getClass())) {
            binder.setValidator(courseCreateDtoValidator);
        }
    }
    //------------------------------------------------------------------------------

    @GetMapping("/{courseId}")
    @Operation(summary = "Returns a specific course by their identifier.")

    public ResponseEntity<ApiResponse<CourseDto>> getCourse(
            @Parameter(description="courseId of the course to be obtained. Cannot be empty.",
                    required=true)
            @PathVariable String courseId){


        if(!UUIDValidator.isValidUUID(courseId)){
            throw new IllegalArgumentException("courseId must be a valid UUID");
        }

        Optional<Course> courseOptional = courseService.findByCourseId(new CourseId(courseId));
        if (courseOptional.isPresent()){

            ApiResponse<CourseDto> apiResponse = new ApiResponse<>(CourseDto.of(courseOptional.get()));

                return ResponseEntity
                        .ok()
                        .body(apiResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Add Course.")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(
            @Parameter(description="The CourseDto request. Cannot be empty.",
                    required=true, content = @Content(schema = @Schema(implementation = CourseDto.class)))
            @Valid @RequestBody CourseDto request) {
        LOGGER.info("Attempting to create course {}", request.getCourseId());

        Result<CourseDto> result = courseService.save(request);

        if(!result.isOk()){
            return ResponseEntity.badRequest().body(new ApiResponse<>(result.getErrors()));
        }

        try {
            return ResponseEntity
                    .created(new URI("/courses/"+result.getTarget().getCourseId()))
                    .body(new ApiResponse<>(result.getTarget()));
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
