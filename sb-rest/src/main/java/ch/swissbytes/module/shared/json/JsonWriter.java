package ch.swissbytes.module.shared.json;

import com.google.gson.Gson;

/**
 * Created by jorgeburgos on 2/1/16.
 */
public class JsonWriter {
    private JsonWriter() {

    }

    public static String writeToString(Object object) {
        if (object == null) {
            return "";
        }

        return new Gson().toJson(object);
    }
}
