package ch.swissbytes.module.shared.exception;

import javax.ejb.ApplicationException;

/**
 * Created by jorgeburgos on 2/1/16.
 */
@ApplicationException
public class FieldNotValidException extends RuntimeException {
    private final String fieldName;

    public FieldNotValidException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "FieldNotValidException{" +
                "fieldName='" + fieldName + '\'' +
                '}';
    }
}
