package com.registration.model;

import com.registration.repository.CourseIdConverter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @Column (unique = true, nullable = false)
    private String name;

    @Column(unique = true, updatable = false, nullable = false)
    @Convert(converter = CourseIdConverter.class)
    private CourseId courseId;

    @OneToMany(mappedBy = "course",fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    Set<CourseEnrollment> courseEnrollments = new HashSet<>();

    public Course() {
    }

    public Course(Long id, String name, CourseId courseId) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
    }


    public Course(String name, CourseId courseId) {
        this(null, name, courseId);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourseId getCourseId() {
        return courseId;
    }

    public void setCourseId(CourseId courseId) {
        this.courseId = courseId;
    }

    public Set<CourseEnrollment> getCourseEnrollments() {
        return courseEnrollments;
    }

    public void setCourseEnrollments(Set<CourseEnrollment> courseEnrollments) {
        this.courseEnrollments = courseEnrollments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name) && Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, courseId);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("courseId", courseId)
                .toString();
    }
}
