package ch.swissbytes.module.shared.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;

public class FilterCriteria {

    private final Object value;

    private final Object value2;

    private final FilterType type;

    private FilterPredicate predicate;

    public FilterCriteria() {
        type = null;
        value = null;
        predicate = null;
        value2 = null;
    }

    public FilterCriteria(final FilterType type, final Object value) {
        this.type = type;
        this.value = value;
        value2 = null;
    }

    public FilterCriteria(final FilterType type, final FilterPredicate predicate, final Object value, final Object value2) {
        this.type = type;
        this.value = value;
        this.value2 = value2;
        this.predicate = predicate;
    }

    public FilterCriteria(final FilterType type, final FilterPredicate predicate, final Object value) {
        this.type = type;
        this.predicate = predicate;
        this.value = value;
        this.value2 = null;
    }

    public FilterType getType() {
        return type;
    }

    public FilterPredicate getPredicate() {
        return predicate;
    }

    public Object getValue() {
        return value;
    }

    public boolean isValid() {
        return value != null && type != null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Predicate createPredicate(final Path field, final CriteriaBuilder cb) {
        Expression param = cb.upper(cb.concat(cb.toString(field), ""));
        switch (type) {
            case NOT_LIKE:
                return cb.notLike(param, String.format("%%%s%%", ((String) value)).toUpperCase());
            case NOT_EQUAL:
                return cb.notEqual(param, value);
            case IS_NULL:
                return cb.isNull(field);
            case IS_NOT_NULL:
                return cb.isNotNull(field);
            case EQUAL:
                return cb.equal(param, String.format("%s", value).toUpperCase());
            case NOT_IN_LIST:
                return cb.not(field.in(value));
            case BETWEEN_DATES:
                if (value instanceof Date) {
                    return cb.between(param, (Date) value, (Date) value2);
                }
                return cb.between(param, String.format("%s", value), String.format("%s", value2));
            default:
                throw new IllegalArgumentException("Filtro invalido");
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FilterCriteria{");
        sb.append("value=").append(value);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }

    public enum FilterType {
        NOT_LIKE, NOT_EQUAL, IS_NULL, IS_NOT_NULL, EQUAL, NOT_IN_LIST, BETWEEN_DATES;
    }

    public enum FilterPredicate {
        AND, OR;
    }
}
