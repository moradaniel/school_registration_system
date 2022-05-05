package com.registration.repository;


import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.List;

public class RepositoryHelper {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryHelper.class);

    private RepositoryHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static List findByNamedParam(EntityManager entityManager, final String queryString, final List<String> paramNames, final List<Object> values/*, final Type[] types*/, final Integer startIndex, final Integer maxResults)/* throws DataAccessException*/ {
        Query query = buildQuerByNamedParam(entityManager, queryString, paramNames, values, startIndex, maxResults);
        return query.getResultList();
    }

    private static Query buildQuerByNamedParam(EntityManager entityManager, final String queryString, final List<String> paramNames, final List<Object> values/*, final Type[] types*/, final Integer startIndex, final Integer maxResults)/* throws DataAccessException*/ {

        if (paramNames.size() != values.size()) {
            throw new IllegalArgumentException("Length of paramNames list must match length of values list");
        }

        Query query = entityManager.createQuery(queryString);

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        if (startIndex != null) {
            query.setFirstResult(startIndex);
        }

        for (int i = 0; i < values.size(); i++) {
            applyNamedParameterToQuery(query, paramNames.get(i), values.get(i));
        }
        return query;
    }

    public static long countByNamedParam(EntityManager entityManager, final String queryString, final List<String> paramNames, final List<Object> values) {

        String queryStringWithNoFetchs = queryString.replace("FETCH", " ").replace("fetch", " ");

        Query query = buildQuerByNamedParam(entityManager, queryStringWithNoFetchs, paramNames, values, null, null);

        return (long) query.getSingleResult();

    }

    public static Object uniqueElement(List list) throws NonUniqueResultException {
        int size = list.size();
        if (size == 0)
            return null;
        Object first = list.get(0);
        for (int i = 1; i < size; i++) {
            if (list.get(i) != first) {
                throw new NonUniqueResultException("non uniqueElement, size: " + list.size());
            }
        }
        return first;
    }


    public static void applyNamedParameterToQuery(Query queryObject, String paramName, Object value/*, Type type*/) {

        queryObject.setParameter(paramName, value);

    }


    public static NativeQuery buildSQLQueryByNamedParam(EntityManager entityManager, final String queryString, final List<String> paramNames,
                                                        final List<Object> values, final Integer startIndex, final Integer maxResults, boolean isCount, String entityType) {

        if (paramNames.size() != values.size()) {
            throw new IllegalArgumentException("Length of paramNames list must match length of values list");
        }

        NativeQuery sqlQuery = ((Session) entityManager.getDelegate()).createSQLQuery(queryString);

        if (!isCount) {
            try {
                sqlQuery.addEntity("c", Class.forName(entityType));
            } catch (ClassNotFoundException e) {
                logger.info(e.getMessage());
            }
        }

        if (maxResults != null) {
            sqlQuery.setMaxResults(maxResults);
        }

        if (startIndex != null) {
            sqlQuery.setFirstResult(startIndex);
        }

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                applyNamedParameterToQuery(sqlQuery, paramNames.get(i), values.get(i));
            }
        }
        return sqlQuery;
    }

    public static void applyNamedParameterToQuery(NativeQuery queryObject, String paramName, Object value) {

        queryObject.setParameter(paramName, value);

    }
}
