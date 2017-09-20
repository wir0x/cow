package ch.swissbytes.module.shared.rest;

/**
 * check http://racksburg.com/choosing-an-http-status-code/
 * Created by jorgeburgos on 2/1/16.
 */
public enum HttpCode {
    CREATED(201),
    NO_CONTENT(204),
    VALIDATION_ERROR(422),
    OK(200),
    NOT_FOUND(404),
    FORBIDDEN(403),
    INTERNAL_ERROR(500),
    UNAUTHORIZED(401);

    private int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
