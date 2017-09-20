package ch.swissbytes.module.shared.json;

import ch.swissbytes.module.shared.json.exception.InvalidJsonException;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class JsonReader {
    public static JsonObject readAsJsonObject(String json) {
        return readJsonAs(json, JsonObject.class);
    }

    private static <T> T readJsonAs(String json, Class<T> jsonClass) {
        if (json == null || json.trim().isEmpty()) {
            throw new InvalidJsonException("Json String can not be null");
        }

        try {
            return new Gson().fromJson(json, jsonClass);
        } catch (JsonSyntaxException e) {
            throw new InvalidJsonException(e);
        }
    }

    public static String getStringOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsString();
    }

    public static List<String> getSListOfLongOrNull(final JsonObject jsonObject, final String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        List<String> list = new ArrayList<>();
        JsonArray jsonArray = property.getAsJsonArray();

        for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.toString());
        }

        return list;
    }

    public static Integer getIntegerOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsInt();
    }

    public static Long getLongOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsLong();
    }

    public static Boolean getBooleanOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsBoolean();
    }

    public static Double getDoubleOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsDouble();
    }

    public static JsonArray getJsonArrayOrNull(JsonObject jsonObject, String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if (isJsonElementNull(property)) {
            return null;
        }

        return property.getAsJsonArray();
    }

    private static boolean isJsonElementNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }
}
