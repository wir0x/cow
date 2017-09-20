package ch.swissbytes.module.shared.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public interface EntityJsonConverter<T> {
    T convertFrom(String Json);

    JsonElement convertToJsonElement(T entity);

    default JsonElement convertToJsonElement(List<T> entities) {
        JsonArray jsonArray = new JsonArray();

        for (T entity : entities) {
            jsonArray.add(convertToJsonElement(entity));
        }

        return jsonArray;
    }
}
