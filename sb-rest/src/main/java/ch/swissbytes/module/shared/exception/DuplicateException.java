package ch.swissbytes.module.shared.exception;

import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String msg) {
        super(msg);
    }

    public DuplicateException(PersistenceException pEx) {
        if (pEx.getCause() instanceof ConstraintViolationException) {
            throw new DuplicateException("El registro se encuentra duplicado");
        }
        throw new PersistenceException(pEx);
    }
}

