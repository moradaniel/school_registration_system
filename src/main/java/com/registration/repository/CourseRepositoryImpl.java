package com.registration.repository;

import com.registration.filter.CourseFilter;
import com.registration.filter.StudentFilter;
import com.registration.model.Course;
import com.registration.model.CourseId;
import com.registration.model.StudentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;


public class CourseRepositoryImpl implements CourseRepositoryCustom {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Course> loadByCourseId(CourseId courseId) {
        int page = 0;
        int size = 1;

        CourseFilter courseFilter = new CourseFilter();
        courseFilter.addCourseIds(courseId);
        courseFilter.setFacets(Arrays.asList(CourseFilter.CourseFacet.values()));

        PageImpl<Course> result = findByCriteria(courseFilter, PageRequest.of(page,size));

        return result.getContent().stream().findFirst();
    }

    @Override
    public PageImpl<Course> findByCriteria(CourseFilter courseFilter, Pageable pageable) {
        Objects.requireNonNull(courseFilter);
        Objects.requireNonNull(pageable);

        List<String> wheres = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();


        String select = " Select distinct course ";
        String countSelect = " Select count(distinct course) ";

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(" from Course course ");

        if(courseFilter.getCourseFacets().contains(StudentFilter.StudentFacet.COURSE_REGISTRATION)){
            queryBuilder.append(" left join fetch course.courseEnrollments courseEnrollments ");
            queryBuilder.append(" left join fetch courseEnrollments.student student");
        }

        buildWhereClause(courseFilter,wheres,paramNames,values);

        if(CollectionUtils.isEmpty(wheres)){
            return buildEmptyPage(Course.class);
        }

        String queryWithoutOrdering = wheres.isEmpty() ? queryBuilder.toString() : queryBuilder.append(" WHERE ").append(
                com.registration.util.StringUtils.getStringsSeparatedBy(" AND ", wheres)).toString();


        String queryWithOrdering = queryWithoutOrdering;


        return new PageImpl<>(
                RepositoryHelper.findByNamedParam(entityManager,select+queryWithOrdering, paramNames, values, pageable != null ? (int)pageable.getOffset() : null, pageable != null ? pageable.getPageSize() : null),
                pageable,
                RepositoryHelper.countByNamedParam(entityManager,countSelect+queryWithoutOrdering, paramNames, values)
        );



    }

    private void buildWhereClause(CourseFilter courseFilter,
                                  List<String> wheres, List<String> paramNames, List<Object> values) {
        if (courseFilter != null) {

            List<CourseId> courseIds = courseFilter.getCourseIds();

            if(!CollectionUtils.isEmpty(courseIds)){
                wheres.add("  course.courseId IN ( :courseIds )  ");
                paramNames.add("courseIds");
                values.add(courseIds);
            }

            if(courseFilter.getCourseFacets().contains(CourseFilter.CourseFacet.COURSE_REGISTRATION)) {
                List<StudentId> studentIds = courseFilter.getStudentIds();
                  if (!CollectionUtils.isEmpty(studentIds)) {
                    wheres.add("  student.studentId IN ( :studentIds )  ");
                    paramNames.add("studentIds");
                    values.add(studentIds);
                }
            }


            String name = courseFilter.getName();
            if(StringUtils.hasText(name)) {
                StringBuilder sb = new StringBuilder();
                sb.append(" lower(course.name) like :name ");
                wheres.add(sb.toString());
                paramNames.add("name");
                values.add("%"+courseFilter.getName().toLowerCase()+"%");
            }


        }
    }

    private <T> PageImpl<T> buildEmptyPage(Class<T> targetClass){
        List<T> content = new ArrayList<>();
        return new PageImpl<>(content);
    }



    private static Query buildSQLQueryByNamedParam(EntityManager entityManager, final String queryString, final List<String> paramNames,
                                                     final List<Object> values, final Integer startIndex, final Integer maxResults, boolean isCount, String alias,  Class klass) {

        if (paramNames.size() != values.size()) {
            throw new IllegalArgumentException("Length of paramNames list must match length of values list");
        }

        Query sqlQuery = null;


        if(!isCount) {
            if(klass != null) {
                sqlQuery = entityManager.createNativeQuery(queryString, klass);
            }
        }else{
            sqlQuery = entityManager.createNativeQuery(queryString);
        }

        if (maxResults != null) {
            sqlQuery.setMaxResults(maxResults);
        }

        if (startIndex != null) {
            sqlQuery.setFirstResult(startIndex);
        }

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                applyNamedParameterToQuery( sqlQuery, paramNames.get(i), values.get(i));
            }
        }
        return sqlQuery;
    }

    public static  void applyNamedParameterToQuery(Query queryObject, String paramName, Object value) {

        queryObject.setParameter(paramName, value);

    }

    public static long countByNamedParamForNativeQuery(EntityManager entityManager, final String queryString, final List<String> paramNames,
                                                       final List<Object> values, String alias, Class klass){
        Query sqlQuery = buildSQLQueryByNamedParam(entityManager, queryString, paramNames, values, null, null, true, alias, klass);
        Object countR = sqlQuery.getSingleResult();
        return ((Number) countR).longValue();
    }



}