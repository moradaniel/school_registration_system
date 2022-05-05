package com.registration.dto.enrollment.create;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registration.model.Course;
import com.registration.model.Student;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

public class CreateCourseEnrollmentRequestDto {
    @NotBlank
    private String courseId;

    @NotBlank
    private String studentId;


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CreateCourseEnrollmentRequestDto(@JsonProperty("courseId")String courseId,
                                            @JsonProperty("studentId")String studentId) {
        UUID.fromString(courseId);
        this.courseId = courseId;

        UUID.fromString(studentId);
        this.studentId = studentId;
    }

    public String getCourseId() { return courseId; }

    public String getStudentId() { return studentId; }

    public static CreateCourseEnrollmentRequestDto of(Course course, Student student) {
        return new CreateCourseEnrollmentRequestDto(course.getCourseId().asString(), student.getStudentId().asString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCourseEnrollmentRequestDto that = (CreateCourseEnrollmentRequestDto) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, studentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("courseId", courseId)
                .append("studentId", studentId)
                .toString();
    }
}
