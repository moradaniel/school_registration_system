package com.registration.repository;

import com.registration.filter.CourseEnrollmentFilter;
import com.registration.model.CourseEnrollment;
import com.registration.model.CourseId;
import com.registration.model.StudentId;
import com.registration.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


public class CourseEnrollmentRepositoryImpl implements CourseEnrollmentRepositoryCustom {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CourseEnrollment> findByCriteria(CourseEnrollmentFilter courseEnrollmentFilter, Pageable pageable) {


        List<String> wheres = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        String select = " Select distinct courseEnrollment ";
        String countSelect = " Select count(distinct courseEnrollment) ";

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(" from CourseEnrollment courseEnrollment ");
        queryBuilder.append(" left join fetch courseEnrollment.student student");
        queryBuilder.append(" left join fetch courseEnrollment.course course");

        buildWhereClause(courseEnrollmentFilter,wheres,paramNames,values);


        String queryWithoutOrdering = wheres.isEmpty() ? queryBuilder.toString() : queryBuilder.append(" WHERE ").append(
                StringUtils.getStringsSeparatedBy(" AND ", wheres)).toString();


        String queryWithOrdering = queryWithoutOrdering;


        return new PageImpl<>(
                RepositoryHelper.findByNamedParam(entityManager,select+queryWithOrdering, paramNames, values, pageable != null ? (int)pageable.getOffset() : null, pageable != null ? pageable.getPageSize() : null),
                pageable,
                RepositoryHelper.countByNamedParam(entityManager,countSelect+queryWithoutOrdering, paramNames, values)
        );



    }

    private void buildWhereClause(CourseEnrollmentFilter courseEnrollmentFilter,
                                  List<String> wheres, List<String> paramNames, List<Object> values) {
        if (courseEnrollmentFilter != null) {

            List<Long> studentIds = courseEnrollmentFilter.getStudentIds();
            if(!CollectionUtils.isEmpty(studentIds)){
                wheres.add("  courseEnrollment.student.id IN ( :studentIds )  ");
                paramNames.add("studentIds");
                values.add(studentIds);
            }

            List<Long> courseIds = courseEnrollmentFilter.getCourseIds();
            if(!CollectionUtils.isEmpty(courseIds)){
                wheres.add("  courseEnrollment.course.id IN ( :courseIds )  ");
                paramNames.add("courseIds");
                values.add(courseIds);
            }


            List<StudentId> studentStudentIds = courseEnrollmentFilter.getStudentStudentIds();
            if(!CollectionUtils.isEmpty(studentStudentIds)){
                wheres.add("  courseEnrollment.student.studentId IN ( :studentStudentIds )  ");
                paramNames.add("studentStudentIds");
                values.add(studentStudentIds);
            }

            List<CourseId> courseCourseIds = courseEnrollmentFilter.getCourseCourseIds();
            if(!CollectionUtils.isEmpty(courseCourseIds)){
                wheres.add("  courseEnrollment.course.courseId IN ( :courseCourseIds )  ");
                paramNames.add("courseCourseIds");
                values.add(courseCourseIds);
            }

        }
    }





}