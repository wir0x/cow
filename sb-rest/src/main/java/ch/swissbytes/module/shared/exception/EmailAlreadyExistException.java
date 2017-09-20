package ch.swissbytes.module.shared.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class EmailAlreadyExistException extends RuntimeException {
}
