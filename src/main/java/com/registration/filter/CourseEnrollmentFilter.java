package com.registration.filter;


import com.registration.model.CourseId;
import com.registration.model.StudentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CourseEnrollmentFilter implements Serializable {


    private static final long serialVersionUID = 1L;

    private List<Long> studentIds = new ArrayList<>();

    private List<Long> courseIds = new ArrayList<>();

    private List<StudentId> studentStudentIds = new ArrayList<>();

    private List<CourseId> courseCourseIds = new ArrayList<>();

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public void addStudentIds(Long ... studentIds) {
        this.studentIds.addAll(Arrays.asList(studentIds));
    }



    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }


    public void addCourseIds(Long ... courseIds) {
        this.courseIds.addAll(Arrays.asList(courseIds));
    }


    public List<StudentId> getStudentStudentIds() {
        return studentStudentIds;
    }

    public void setStudentStudentIds(List<StudentId> studentStudentIds) {
        this.studentStudentIds = studentStudentIds;
    }

    public List<CourseId> getCourseCourseIds() {
        return courseCourseIds;
    }

    public void setCourseCourseIds(List<CourseId> courseCourseIds) {
        this.courseCourseIds = courseCourseIds;
    }
}
