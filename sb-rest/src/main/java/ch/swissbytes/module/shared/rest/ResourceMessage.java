package ch.swissbytes.module.shared.rest;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class ResourceMessage {
    private static final String KEY_EXISTENT = "%s.existent";
    private static final String MESSAGE_EXISTENT = "Ya exíste un %s con el %s dado ";
    private static final String KEY_INVALID_FIELD = "%s.invalidField.%s";
    private static final String KEY_NOT_FOUND = "%s.NotFound";
    private static final String MESSAGE_NOT_FOUND = "%s not found";
    private static final String NOT_FOUND = "Not Found";
    private static final String COULD_NOT_WRITE_IMAGE_FILE = "Could not write image file";
    private static final String IMAGE_FILE_NOT_PROVIDED = "Image file was not provided";
    private final String resource;

    public ResourceMessage(String resource) {
        this.resource = resource;
    }

    public String getKeyOfResourceExistent() {
        return String.format(KEY_EXISTENT, resource);
    }

    public String getMessageOfResourceExistent(String fieldNames) {
        return String.format(MESSAGE_EXISTENT, resource, fieldNames);
    }

    public String getKeyOfInvalidField(String invalidField) {
        return String.format(KEY_INVALID_FIELD, resource, invalidField);
    }

    public String getKeyOfResourceNotFound() {
        return String.format(KEY_NOT_FOUND, resource);
    }

    public String getMessageOfResourceNotFound() {
        return String.format(MESSAGE_NOT_FOUND, resource);
    }

    public String getMessageNotFound() {
        return NOT_FOUND;
    }

    public String getMessageCouldNotWriteImage() {
        return COULD_NOT_WRITE_IMAGE_FILE;
    }

    public String getMessageImageNotProvided() {
        return IMAGE_FILE_NOT_PROVIDED;
    }
}
