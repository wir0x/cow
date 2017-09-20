package ch.swissbytes.module.shared.exception;

import ch.swissbytes.domain.enumerator.RespErrorEnum;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class InvalidInputException extends EJBException {

    private Integer codError;
    private String message;

    public InvalidInputException(String msg) {
        message = msg;
    }

    public InvalidInputException(EJBException ejbEx) {
        if (ejbEx.getCausedByException() instanceof ConstraintViolationException) {
            ConstraintViolationException cvEx = (ConstraintViolationException) ejbEx.getCausedByException();
            loadCodeAndMessage(cvEx);
        } else if (ejbEx.getCausedByException() instanceof DuplicateException) {
            DuplicateException duplicateException = (DuplicateException) ejbEx.getCausedByException();
            loadCodeAndMessage(duplicateException);
        } else {
            codError = RespErrorEnum.REGISTER_ERROR.getId();
            message = ejbEx.getMessage();
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCodError() {
        return codError;
    }

    public void loadCodeAndMessage(ConstraintViolationException cvEx) {
        StringBuilder errorMessage = new StringBuilder();

        for (ConstraintViolation<?> constraintViolation : cvEx.getConstraintViolations()) {
            errorMessage.append(constraintViolation.getMessage());
            errorMessage.append("\n");
        }
        codError = RespErrorEnum.DATA_ERROR.getId();
        message = errorMessage.toString();
    }

    public void loadCodeAndMessage(DuplicateException duplicateException) {
        codError = RespErrorEnum.DUPLICATE_ERROR.getId();
        message = duplicateException.getMessage();
    }
}

