package ch.swissbytes.module.shared.persistence;

import ch.swissbytes.module.shared.utils.OrderBy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Repository implements Serializable {

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Returns entities matching with the given classType
     * and the given ID, as all IDs are number-based they are all mapped
     * as Long, so there's no need to handle them as objects.
     * <pre>
     * Usage:
     *  List&lt;TUser&gt; users = repository.findById( TUser.class , 1L );
     * </pre>
     *
     * @param clazz entity target class
     * @param id    not-null Long number
     * @return a list of elements matching the ID-equals criteria
     */
    public <T> List<T> findById(final Class<T> clazz, final Long id) {
        return findEquals(clazz, "id", id);
    }

    /**
     * Return a single entities matching with the given classType
     * and the given ID, as all IDs are number-based they are all mapped
     * as Long, so there's no need to handle them as objects.
     * <pre>
     * Usage:
     * Optional&lt;TUser&gt; result = repository.getById( TUser.class, 1L);
     * if( result.isPresent() ){
     *   TUser user = result.get();
     * }
     * </pre>
     *
     * @param clazz entity target class
     * @param id    not-null Long number
     * @return Optional element of the result, {@link Optional#ABSENT} if there
     * are not results
     */
    public <T> Optional<T> getById(final Class<T> clazz, final Long id) {
        return Optional.firstElement(findById(clazz, id));
    }

    /**
     * Returns a single entity matching with the given classType
     * and the property equality.
     *
     * @param clazz        entity target class
     * @param propertyName property/field name to compare
     * @param value        value of the property to compare with
     * @return Optional element of the result, {@link Optional#ABSENT} if there
     * are not results
     */
    public <T> Optional<T> getBy(final Class<T> clazz, final String propertyName, final Object value) {
        return Optional.firstElement(findEquals(clazz, propertyName, value));
    }

    /**
     * Returns entities matching with the given classType
     * and the given property.
     * <pre>
     * Usage:
     *  List&lt;TUser&gt; users = repository.findById( TUser.class , "login" , "admin" );
     * </pre>
     *
     * @param clazz        entity target class
     * @param propertyName
     * @param value
     * @return a list of elements matching the property-equals criteria
     * @author timoteo
     */
    public <T> List<T> findEquals(final Class<T> clazz, final String propertyName, final Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.where(cb.equal(root.get(propertyName), value));
        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findNotEquals(final Class<T> clazz, final String propertyName, final Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        query.where(cb.notEqual(root.get(propertyName), value));
        return entityManager.createQuery(query).getResultList();
    }

    public <T> Number countAll(final Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        query.from(clazz);
        query.select(cb.count(query.from(clazz)));
        return entityManager.createQuery(query).getSingleResult();
    }

    public <T> Number countEquals(final Class<T> clazz, final String propertyName, final Object value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        query.select(cb.count(query.from(clazz)));
        query.where(cb.equal(root.get(propertyName), value));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * @param clazz
     * @param propertyName
     * @param value
     * @return
     */
    public <T> boolean exists(final Class<T> clazz, final String propertyName, final Object value) {
        return countEquals(clazz, propertyName, value).intValue() > 0;
    }

    /**
     * @param clazz
     * @return
     */
    public <T> List<T> findAll(final Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        query.from(clazz);
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * @param clazz
     * @param entityLazy
     * @return
     */
    public <T> List<T> findAllWithLazyJoin(final Class<T> clazz, final String entityLazy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        root.fetch(entityLazy, JoinType.LEFT);
        query.select(root).distinct(true);
        return entityManager.createQuery(query).getResultList();
    }

    private <T> Predicate composePredicate(CriteriaBuilder cb, Root<T> root, Map<String, Object> filters) {
        Predicate predicate = cb.conjunction();
        Predicate predicateOR = null;
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            Expression param = cb.upper(cb.concat(cb.toString(field), ""));
            if (value instanceof Collection) {
                predicate = cb.and(predicate, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    if (FilterCriteria.FilterPredicate.OR.equals(filterCriteria.getPredicate())) {
                        predicateOR = predicateOR == null ? cb.disjunction() : predicateOR;
                        predicateOR = cb.or(predicateOR, filterCriteria.createPredicate(field, cb));
                    } else {
                        predicate = cb.and(predicate, filterCriteria.createPredicate(field, cb));
                    }
                }
            } else if (value instanceof String) {
                predicate = cb.and(predicate, cb.like(param, String.format("%%%s%%", value).toUpperCase()));
            } else {
                predicate = cb.and(predicate, cb.equal(param, String.format("%s", value).toUpperCase()));
            }
        }

        if (predicateOR != null) {
            return cb.and(predicate, predicateOR);
        }
        return predicate;
    }

    public <T> List<T> findAll(final Class<T> clazz, int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicate = composePredicate(cb, root, filters);

        // sort
        if (sortField != null && sortOrder != null) {
            switch (sortOrder) {
                case "ASCENDING":
                    query.orderBy(cb.asc(field(root, sortField)));
                    break;
                case "DESCENDING":
                    query.orderBy(cb.desc(field(root, sortField)));
                    break;
                default:
                    break;
            }
        }

        query.where(predicate);
        Query q = entityManager.createQuery(query);

        // pagination
        if (pageSize >= 0) {
            q.setMaxResults(pageSize);
        }
        if (first >= 0) {
            q.setFirstResult(first);
        }

        return q.getResultList();
    }

    public <T> List<T> findAll(final Class<T> clazz, int first, int pageSize, List<OrderBy> multipleSort, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicate = composePredicate(cb, root, filters);

        // sort
        List<Order> orderList = new ArrayList<>();

        for (OrderBy sort : multipleSort) {
            if (sort.getSortField() != null && sort.getSortOrder() != null) {
                switch (sort.getSortOrder()) {
                    case "ASCENDING":
                        orderList.add(cb.asc(field(root, sort.getSortField())));
                        break;
                    case "DESCENDING":
                        orderList.add(cb.desc(field(root, sort.getSortField())));
                        break;
                    default:
                        break;
                }
            }
        }
        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }

        query.where(predicate);
        Query q = entityManager.createQuery(query);

        // pagination
        if (pageSize >= 0) {
            q.setMaxResults(pageSize);
        }
        if (first >= 0) {
            q.setFirstResult(first);
        }

        return q.getResultList();
    }

    public <T> int count(final Class<T> clazz, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(clazz);
        cq.select(cb.count(root));

        // filter
        Predicate predicate = composePredicate(cb, root, filters);

        cq.where(predicate);
        Long size = (Long) entityManager.createQuery(cq).getSingleResult();
        return size.intValue();
    }

    private Path field(Path root, String filter) {
        if (!filter.contains(".")) {
            return root.get(filter);
        } else {
            String[] parts = filter.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                root = root.get(parts[i]);
            }
        }
        return root;
    }

    public <T> List<T> findAllGlobalFilter(final Class<T> clazz, int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicateOR = cb.disjunction();
        Predicate predicateAND = cb.conjunction();

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            Expression param = cb.upper(cb.concat(cb.toString(field), ""));
            if (value instanceof Collection) {
                predicateOR = cb.or(predicateOR, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    if (FilterCriteria.FilterPredicate.AND.equals(filterCriteria.getPredicate())) {
                        predicateAND = cb.and(predicateAND, filterCriteria.createPredicate(field, cb));
                    } else {
                        predicateOR = cb.or(predicateOR, filterCriteria.createPredicate(field, cb));
                    }
                }
            } else if (value instanceof String) {
                predicateOR = cb.or(predicateOR, cb.like(param, String.format("%%%s%%", value).toUpperCase()));
            } else {
                predicateOR = cb.or(predicateOR, cb.equal(param, String.format("%s", value).toUpperCase()));
            }
        }

        // sort
        if (sortField != null && sortOrder != null) {
            switch (sortOrder) {
                case "ASCENDING":
                    query.orderBy(cb.asc(field(root, sortField)));
                    break;
                case "DESCENDING":
                    query.orderBy(cb.desc(field(root, sortField)));
                    break;
                default:
                    break;
            }
        }

        query.where(cb.and(predicateAND, predicateOR));
        Query q = entityManager.createQuery(query);

        // pagination
        if (pageSize >= 0) {
            q.setMaxResults(pageSize);
        }
        if (first >= 0) {
            q.setFirstResult(first);
        }

        return q.getResultList();
    }

    public <T> List<T> findAllGlobalFilter(final Class<T> clazz, int first, int pageSize, List<OrderBy> multipleSort, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicate = cb.disjunction();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            Expression param = cb.upper(cb.concat(cb.toString(field), ""));
            if (value instanceof Collection) {
                predicate = cb.or(predicate, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    predicate = cb.or(predicate, filterCriteria.createPredicate(field, cb));
                }
            } else if (value instanceof String) {
                predicate = cb.or(predicate, cb.like(param, String.format("%%%s%%", value).toUpperCase()));
            } else {
                predicate = cb.or(predicate, cb.equal(param, filter.getValue()));
            }
        }

        // sort
        List<Order> orderList = new ArrayList<>();
        for (OrderBy sort : multipleSort) {
            if (sort.getSortField() != null && sort.getSortOrder() != null) {
                switch (sort.getSortOrder()) {
                    case "ASCENDING":
                        orderList.add(cb.asc(field(root, sort.getSortField())));
                        break;
                    case "DESCENDING":
                        orderList.add(cb.desc(field(root, sort.getSortField())));
                        break;
                    default:
                        break;
                }
            }
        }
        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }

        query.where(predicate);
        Query q = entityManager.createQuery(query);

        // pagination
        if (pageSize >= 0) {
            q.setMaxResults(pageSize);
        }
        if (first >= 0) {
            q.setFirstResult(first);
        }

        return q.getResultList();
    }

    public <T> int countGlobalFilter(final Class<T> clazz, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(clazz);
        cq.select(cb.count(root));

        // filter
        Predicate predicateOR = cb.disjunction();
        Predicate predicateAND = cb.conjunction();

        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            Expression param = cb.upper(cb.concat(cb.toString(field), ""));
            if (value instanceof Collection) {
                predicateOR = cb.or(predicateOR, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    if (FilterCriteria.FilterPredicate.AND.equals(filterCriteria.getPredicate())) {
                        predicateAND = cb.and(predicateAND, filterCriteria.createPredicate(field, cb));
                    } else {
                        predicateOR = cb.or(predicateOR, filterCriteria.createPredicate(field, cb));
                    }
                }
            } else if (value instanceof String) {
                predicateOR = cb.or(predicateOR, cb.like(param, String.format("%%%s%%", value).toUpperCase()));
            } else {
                predicateOR = cb.or(predicateOR, cb.equal(param, String.format("%s", value).toUpperCase()));
            }
        }
        cq.where(cb.and(predicateAND, predicateOR));
        Long size = (Long) entityManager.createQuery(cq).getSingleResult();
        return size.intValue();
    }

    /**
     * @param entity
     */
    public <T> T save(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public <T> void saveAll(final Collection<T> entities) {
        for (T item : entities) {
            entityManager.persist(item);
        }
    }

    public <T> T saveAndFlush(final T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    public <T> void saveAndFlushAll(final Collection<T> entities) {
        for (T item : entities) {
            entityManager.persist(item);
        }
        entityManager.flush();
    }

    /**
     * @param entity
     * @return
     */

    public <T> T merge(final T entity) {
        return entityManager.merge(entity);
    }

    public <T> void update(T entity) {
        entityManager.merge(entity);
    }

    public <T> void remove(final T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    /**
     * @param entity
     */
    public <T> void delete(final List<T> entity) {
        entity.forEach(this::remove);
    }

    public <T> List<T> findBy(final String queryStr, Map<String, Object> values) {
        Query query = entityManager.createQuery(queryStr);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    public <T> List<T> findBy(final Class<T> clazz, Map<String, Object> values) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicate = cb.conjunction();
        for (Map.Entry<String, Object> filter : values.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            if (value instanceof Collection) {
                predicate = cb.and(predicate, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    predicate = cb.and(predicate, filterCriteria.createPredicate(field, cb));
                }
            } else {
                predicate = cb.and(predicate, cb.equal(field, filter.getValue()));
            }
        }
        query.where(predicate);
        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findBy(final Class<T> clazz, Map<String, Object> values, List<OrderBy> multipleSort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        // filter
        Predicate predicate = cb.conjunction();
        for (Map.Entry<String, Object> filter : values.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            if (value instanceof Collection) {
                predicate = cb.and(predicate, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    predicate = cb.and(predicate, filterCriteria.createPredicate(field, cb));
                }
            } else {
                predicate = cb.and(predicate, cb.equal(field, filter.getValue()));
            }
        }

        // sort
        List<Order> orderList = new ArrayList<>();

        for (OrderBy sort : multipleSort) {
            if (sort.getSortField() != null && sort.getSortOrder() != null) {
                switch (sort.getSortOrder()) {
                    case OrderBy.ASCENDING:
                        orderList.add(cb.asc(field(root, sort.getSortField())));
                        break;
                    case OrderBy.DESCENDING:
                        orderList.add(cb.desc(field(root, sort.getSortField())));
                        break;
                    default:
                        break;
                }
            }
        }

        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }

        query.where(predicate);
        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findBy(final Class<T> clazz, Map<String, Object> values, String entityLazy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        root.fetch(entityLazy, JoinType.LEFT);

        // filter
        Predicate predicate = cb.conjunction();
        for (Map.Entry<String, Object> filter : values.entrySet()) {
            Object value = filter.getValue();
            Path field = field(root, filter.getKey());
            if (value instanceof Collection) {
                predicate = cb.and(predicate, field.in(filter.getValue()));
            } else if (value instanceof FilterCriteria) {
                FilterCriteria filterCriteria = (FilterCriteria) value;
                if (filterCriteria.isValid()) {
                    predicate = cb.and(predicate, filterCriteria.createPredicate(field, cb));
                }
            } else {
                predicate = cb.and(predicate, cb.equal(field, filter.getValue()));
            }
        }

        query.select(root).where(predicate).distinct(true);
        return entityManager.createQuery(query).getResultList();
    }

    public <T> Optional<T> getBy(final String queryStr, Map<String, Object> values) {
        Query query = entityManager.createQuery(queryStr);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return Optional.firstElement(query.setMaxResults(1).getResultList());
    }

    /**
     * Evaluates if a given specification is satisfied by checking the amount of
     * results,
     * if there are results present the specification is satisfied.
     *
     * @param properties to delegate filtering
     * @return evaluation of the specification results, returning TRUE if it has
     * any
     * @author timoteo
     * public <T> boolean isSatisfied(final Specification<T> specification) {
     * return countOfSpec(specification) > 0;
     * }
     */

    public <T> Optional<T> getBy(final Class<T> targetClass, final Map<String, Object> properties) {
        return Optional.firstElement(findBy(targetClass, properties));
    }

    public <T> Optional<T> getBy(final Class<T> targetClass, final Map<String, Object> properties, final List<OrderBy> multipleSort) {
        return Optional.firstElement(findBy(targetClass, properties, multipleSort));
    }

         /*
         @Override public <T> long countOfSpec(final Specification<T> spec) {
         CriteriaBuilder cb = entityManager.getCriteriaBuilder();
         CriteriaQuery<T> query = specification.buildQuery(cb);
         query.select(cb.count(query.from(spec.targetClass())));
         return (Number) entityManager.createQuery(query).getSingleResult();
         }

         @Override public <T extends Entity> Collection<T> findByIdIn(final Class<T> targetClass, final List<Long> iDs) {
         Criteria criteria = sessionFactory.getCurrentSession().createCriteria(targetClass);
         criteria.add(Restrictions.in("id", iDs));
         return criteria.list();
         }
         */
}
