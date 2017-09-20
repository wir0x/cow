package ch.swissbytes.module.shared.utils;

import java.math.BigDecimal;
import java.util.Date;

public class EntityUtil {
    public static final Integer DEFAULT_INTEGER = 0;
    public static final Long DEFAULT_LONG = 0L;
    public static final Double DEFAULT_DOUBLE = 0D;
    public static final String DEFAULT_STRING = "";
    public static final Date DEFAULT_DATE = new Date();
    public static final Boolean DEFAULT_BOOLEAN = false;
    public static final Boolean DEFAULT_BOOLEAN_TRUE = true;
    public static final BigDecimal DEFAULT_BIG_DECIMAL = new BigDecimal(0);
    public static final String DEFAULT_BUSINESS_NAME = "SIN NOMBRE";

    public static <T> Boolean isNull(T entity) {
        return entity == null;
    }

    public static <T> Boolean isNotNull(T entity) {
        return !isNull(entity);
    }
}

