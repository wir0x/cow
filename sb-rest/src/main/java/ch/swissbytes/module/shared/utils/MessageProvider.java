package ch.swissbytes.module.shared.utils;

import javax.faces.context.FacesContext;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageProvider {

    private ResourceBundle bundle;

    public MessageProvider() {
    }

    public ResourceBundle getBundle() {
        if (bundle == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            bundle = context.getApplication().getResourceBundle(context, "msg");
        }
        return bundle;
    }

    public String getValue(String key) {
        String result = null;
        try {
            result = getBundle().getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
    }
}
