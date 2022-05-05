package com.registration.repository;

import com.registration.model.CourseId;

import javax.persistence.AttributeConverter;

public class CourseIdConverter implements AttributeConverter<CourseId, String> {

    @Override
    public String convertToDatabaseColumn(CourseId courseId) {
        return courseId.asString();
    }

    @Override
    public CourseId convertToEntityAttribute(String courseId) {
        return CourseId.of(courseId);
    }
}