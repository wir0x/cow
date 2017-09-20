package ch.swissbytes.module.shared.json.exception;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class InvalidJsonException extends RuntimeException {
    public InvalidJsonException(String message) {
        super(message);
    }

    public InvalidJsonException(Throwable throwable) {
        super(throwable);
    }
}
