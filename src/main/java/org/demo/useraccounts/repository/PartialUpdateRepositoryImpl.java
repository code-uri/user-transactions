/*
package org.demo.useraccounts.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;

import java.util.Map;

import static org.springframework.data.relational.core.query.Criteria.where;

@NoRepositoryBean
@RequiredArgsConstructor
public class PartialUpdateRepositoryImpl<T, ID> implements PartialUpdateRepository<T, ID> {

    private final EntityInformation<T, ?> entityInformation;
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public T partialUpdate(ID id, Map<String, Object > updates) {
        return createQuery(id, updates).getSingleResult();
    }

    private Prepare<T> createQuery(ID id, Map<String, Object > updates) {
        String entityName = entityInformation.getEntityName();
        Class<T> entityType = entityInformation.getJavaType();

        String queryString = String.format("FROM %s WHERE %s = :value", entityName, fieldName);

        Query query = Query.query(where("id").is(id));

        updates.entrySet().stream().map(e -> Update.update(e.getKey(), e.getValue()))
                .reduce((reduce1, reduce2) ->  reduce1.)


        entityTemplate.update(Query.query(where("").is("")))
        TypedQuery<T> query = manager.createQuery(queryString, entityType);
        return query.setParameter("value", fieldValue);
    }
}*/
