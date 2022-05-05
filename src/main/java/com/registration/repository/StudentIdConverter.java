package com.registration.repository;

import com.registration.model.StudentId;

import javax.persistence.AttributeConverter;

public class StudentIdConverter implements AttributeConverter<StudentId, String> {

    @Override
    public String convertToDatabaseColumn(StudentId studentId) {
        return studentId.asString();
    }

    @Override
    public StudentId convertToEntityAttribute(String studentId) {
        return StudentId.of(studentId);
    }
}