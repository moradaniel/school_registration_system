package com.registration.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "course_enrollment",
        uniqueConstraints = {
        @UniqueConstraint(name = "UniqueStudentCourse", columnNames = {"student_id", "course_id"})
})
public class CourseEnrollment {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id", nullable=false)
    Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="course_id", nullable=false)
    Course course;

    public CourseEnrollment() {
    }


    public CourseEnrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEnrollment that = (CourseEnrollment) o;
        return Objects.equals(student, that.student) && Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course);
    }
}
