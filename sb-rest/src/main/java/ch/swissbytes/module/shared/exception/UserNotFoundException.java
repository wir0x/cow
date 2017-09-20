package ch.swissbytes.module.shared.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class UserNotFoundException extends RuntimeException {
}
