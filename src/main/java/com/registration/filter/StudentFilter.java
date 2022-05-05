package com.registration.filter;


import com.registration.model.StudentId;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class StudentFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum StudentFacet {
        COURSE_REGISTRATION
    }

    private List<StudentId> studentIds = new ArrayList<>();

    String firstName;


    private List<UUID> courseIds = new ArrayList<>();

    public List<StudentId> getStudentIds() {
        return studentIds;
    }

    private String orderBy;
    private String sortBy;

    private List<StudentFacet> studentFacets = new ArrayList<>();


    public void setStudentIds(List<StudentId> studentIds) {
        if(!CollectionUtils.isEmpty(studentIds)) {
            this.studentIds.clear();
            this.studentIds.addAll(studentIds);
        }

    }

    public void addStudentIds(StudentId... studentIds) {
        this.studentIds.addAll(Arrays.asList(studentIds));
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public List<UUID> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<UUID> courseIds) {

        if(!CollectionUtils.isEmpty(courseIds)) {
            this.courseIds.clear();
            this.courseIds.addAll(courseIds);
        }
    }

    public List<StudentFacet> getStudentFacets() {
        return studentFacets;
    }

    public void setFacets(List<StudentFacet> studentFacets) {
        if(!CollectionUtils.isEmpty(studentFacets)) {
            this.studentFacets.clear();
            this.studentFacets.addAll(studentFacets);
        }

    }

    public void addFacets(StudentFacet ... facets) {
        this.studentFacets.addAll(Arrays.asList(facets));
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
