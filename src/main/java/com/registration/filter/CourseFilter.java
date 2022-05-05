package com.registration.filter;


import com.registration.model.CourseId;
import com.registration.model.StudentId;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CourseFilter implements Serializable {

    private static final long serialVersionUID = 1L;
    public enum CourseFacet {
        COURSE_REGISTRATION
    }

    private List<CourseId> courseIds = new ArrayList<>();

    private List<StudentId> studentIds = new ArrayList<>();

    String name;


    private String orderBy;
    private String sortBy;

    private List<CourseFacet> courseFacets = new ArrayList<>();

    public void addCourseIds(CourseId... courseIds) {
        this.courseIds.addAll(Arrays.asList(courseIds));
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<CourseId> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<CourseId> courseIds) {

        if(!CollectionUtils.isEmpty(courseIds)) {
            this.courseIds.clear();
            this.courseIds.addAll(courseIds);
        }
    }

    public List<StudentId> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<StudentId> studentIds) {
        if(!CollectionUtils.isEmpty(studentIds)) {
            this.studentIds.clear();
            this.studentIds.addAll(studentIds);
        }
    }

    public List<CourseFacet> getCourseFacets() {
        return courseFacets;
    }

    public void setFacets(List<CourseFacet> courseFacets) {
        if(!CollectionUtils.isEmpty(courseFacets)) {
            this.courseFacets.clear();
            this.courseFacets.addAll(courseFacets);
        }

    }

    public void addFacets(CourseFacet ... facets) {
        this.courseFacets.addAll(Arrays.asList(facets));
    }


    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
