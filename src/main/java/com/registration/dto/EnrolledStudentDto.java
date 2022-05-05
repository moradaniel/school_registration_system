package com.registration.dto;

import com.fasterxml.jackson.annotation.*;
import java.util.UUID;

public class EnrolledStudentDto {
    private UUID studentID;

    @JsonProperty("studentId")
    public UUID getStudentID() { return studentID; }
    @JsonProperty("studentId")
    public void setStudentID(UUID value) { this.studentID = value; }


}
