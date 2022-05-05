package com.registration.model;


import com.registration.util.UUIDValidator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CourseId implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private final UUID courseId;

    public CourseId(String courseId) {
        if (courseId == null || courseId.isEmpty()) {
            throw new IllegalArgumentException("courseId cannot be null nor empty");
        }
        if (!UUIDValidator.isValidUUID(courseId)) {
            throw new IllegalArgumentException("courseId not valid UUID");
        }
        this.courseId = UUID.fromString(courseId);
    }

    public static CourseId of(String courseId) {
        return new CourseId(courseId);
    }

    public String asString() {
        return courseId.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("courseId", courseId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseId courseId1 = (CourseId) o;
        return Objects.equals(courseId, courseId1.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}