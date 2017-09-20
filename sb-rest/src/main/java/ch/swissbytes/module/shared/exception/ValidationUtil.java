package ch.swissbytes.module.shared.exception;


import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class ValidationUtil {
    public static <T> void validateEntityFields(Validator validator, T entity) {
        Set<ConstraintViolation<T>> errors = validator.validate(entity);
        Iterator<ConstraintViolation<T>> itErrors = errors.iterator();
        if (itErrors.hasNext()) {
            ConstraintViolation<T> violation = itErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }
}
