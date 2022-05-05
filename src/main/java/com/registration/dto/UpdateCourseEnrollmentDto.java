package com.registration.dto;


import com.fasterxml.jackson.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UpdateCourseEnrollmentDto {
    private UUID courseID;
    private Set<EnrolledStudentDto> enrolledStudents = new HashSet<>();

    @JsonProperty("courseId")
    public UUID getCourseID() { return courseID; }
    @JsonProperty("courseId")
    public void setCourseID(UUID value) { this.courseID = value; }

    @JsonProperty("enrolledStudents")
    public Set<EnrolledStudentDto> getEnrolledStudents() { return enrolledStudents; }

    @JsonProperty("enrolledStudents")
    public void setEnrolledStudents(Set<EnrolledStudentDto> enrolledStudents) {
        this.enrolledStudents.clear();
        this.enrolledStudents.addAll(enrolledStudents);
    }


}
