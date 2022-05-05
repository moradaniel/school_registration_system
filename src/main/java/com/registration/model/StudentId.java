package com.registration.model;


import com.registration.util.UUIDValidator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class StudentId implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    private final UUID studentId;

    public StudentId(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new IllegalArgumentException("studentId cannot be null nor empty");
        }
        if (!UUIDValidator.isValidUUID(studentId)) {
            throw new IllegalArgumentException("studentId not valid UUID");
        }
        this.studentId = UUID.fromString(studentId);
    }

    public static StudentId of(String studentId) {
        return new StudentId(studentId);
    }

    public String asString() {
        return studentId.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("studentId", studentId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentId studentId1 = (StudentId) o;
        return Objects.equals(studentId, studentId1.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}