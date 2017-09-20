package ch.swissbytes.module.shared.json;

import ch.swissbytes.module.shared.rest.OperationResult;
import com.google.gson.JsonObject;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class OperationResultJsonWriter {
    private OperationResultJsonWriter() {

    }

    public static String toJson(OperationResult operationResult) {
        return JsonWriter.writeToString(getJsonObject(operationResult));
    }

    private static Object getJsonObject(OperationResult operationResult) {
        if (operationResult.isSuccess()) {
            return getJsonSuccess(operationResult);
        }

        return getJsonError(operationResult);
    }

    private static Object getJsonSuccess(OperationResult operationResult) {
        return operationResult.getEntity();
    }

    private static Object getJsonError(OperationResult operationResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());

        return jsonObject;
    }


}
