package com.registration.model;

import com.registration.repository.StudentIdConverter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(unique = true, updatable = false, nullable = false)
    @Convert(converter = StudentIdConverter.class)
    private StudentId studentId;

    @OneToMany(mappedBy = "student",fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    Set<CourseEnrollment> courseEnrollments = new HashSet<>();


    public Student() {
    }

    public Student(Long id, String firstName, String lastName, StudentId studentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;

    }

    public Student(String firstName, String lastName, StudentId studentId) {
        this(null, firstName, lastName, studentId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public void setStudentId(StudentId studentId) {
        this.studentId = studentId;
    }

    public Set<CourseEnrollment> getCourseEnrollments() {
        return courseEnrollments;
    }

    public void setCourseEnrollments(Set<CourseEnrollment> enrollments) {
        this.courseEnrollments = enrollments;
    }

    public void addCourseEnrollment(CourseEnrollment courseEnrollment) {
        this.getCourseEnrollments().add(courseEnrollment);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("studentId", studentId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, studentId);
    }
}
