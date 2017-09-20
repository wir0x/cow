package ch.swissbytes.module.shared.rest;


import ch.swissbytes.module.shared.exception.FieldNotValidException;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class StandardOperationResults {
    private StandardOperationResults() {
    }

    public static OperationResult getOperationResultExistent(ResourceMessage resourceMessage, String fieldNames) {
        return OperationResult.error(resourceMessage.getKeyOfResourceExistent(),
                resourceMessage.getMessageOfResourceExistent(fieldNames));
    }

    public static OperationResult getImageNotProvided(ResourceMessage resourceMessage) {
        return OperationResult.error("", resourceMessage.getMessageImageNotProvided());
    }

    public static OperationResult getOperationResultInvalidField(ResourceMessage resourceMessage, FieldNotValidException ex) {
        return OperationResult.error(resourceMessage.getKeyOfInvalidField(ex.getFieldName()), ex.getMessage());
    }

    public static OperationResult getOperationResultNotFound(ResourceMessage resourceMessage) {
        return OperationResult.error(resourceMessage.getKeyOfResourceNotFound(),
                resourceMessage.getMessageOfResourceNotFound());
    }

    public static OperationResult getOperationResultDependencyNotFound(ResourceMessage resourceMessage,
                                                                       String dependencyField) {
        return OperationResult.error(resourceMessage.getKeyOfInvalidField(dependencyField),
                resourceMessage.getMessageNotFound());
    }

    public static OperationResult getOperationResultCouldNotWriteImage(ResourceMessage resourceMessage) {
        return OperationResult.error("",
                resourceMessage.getMessageCouldNotWriteImage());
    }

}
