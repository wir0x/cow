package ch.swissbytes.module.shared.utils;

import org.jboss.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
@Named
public class LanguagePreference implements Serializable {

    private static final Logger log = Logger.getLogger(LanguagePreference.class.getName());

    private String language;
    private String timeZone;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        log.info("setLanguage: " + language);
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
