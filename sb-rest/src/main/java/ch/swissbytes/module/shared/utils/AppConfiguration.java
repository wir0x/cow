package ch.swissbytes.module.shared.utils;

import ch.swissbytes.domain.enumerator.TimeMeasurementEnum;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

@Named
public class AppConfiguration implements Serializable {

    private static final Locale locale = new Locale("es", "ES");
    private static final Logger log = Logger.getLogger(AppConfiguration.class.getName());
    private final String languageDefault = "en-AU";

//    private ResourceBundle bundle = ResourceBundle.getBundle("messages_en");
    @Inject
    private LanguagePreference languagePreference;
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
    private int[] sizes = {5, 10, 50, 100};

    public String getPagesSize() {
        return getPagesSize(0L);
    }


    public String getPagesSize(Long total) {
        String sizes = "";
        for (int i = 0; i < this.sizes.length; i++) {
            sizes += this.sizes[i] + " ";
        }
        if (total > this.sizes[this.sizes.length - 1]) {
            sizes += Long.toString(total);
        }
        return sizes.trim();
    }

    public String getDefaultPageSize() {
        return "5";
    }

    public String getTimeMeasurement(TimeMeasurementEnum time) {
        return time != null ? bundle.getString("measurement.time." + time.name().toLowerCase()) : "";
    }

    public Long getTimeOutConversation() {
        return 3600000L;
    }

    public String getPatternDecimal() {
        return "#,##0.00";
    }

    public String getLocale() {
        return "en_AU";
    }

    public String getLangLocalDecimal() {
        return StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : languageDefault;
    }

    public String getLangLocalCalendar() {
        String pattern = languageDefault;
        if (StringUtils.isNotEmpty(languagePreference.getLanguage()) && StringUtils.isNotBlank(languagePreference.getLanguage())) {
            String string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : languageDefault;
            ;
            String[] parts = string.split("-");
            return parts.length > 1 ? parts[0] : languagePreference.getLanguage();
        }
        return pattern;
    }

    public String getFormatDate() {
        log.debug("format date");
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : languageDefault;
        ;
        String[] parts = string.split("-");
        Locale locale;
        if (parts.length > 1) {
            locale = new Locale(parts[0], parts[1]);
        } else {
            locale = new Locale(parts[0], "");
        }
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        String localPattern = ((SimpleDateFormat) formatter).toPattern();
        return localPattern;
    }

    public String getLanguage() {
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : languageDefault;
        ;
        String[] parts = string.split("-");
        return parts != null && parts.length > 0 ? parts[0] : "en";
    }

    public String getCountry() {
        String string = StringUtils.isNotEmpty(languagePreference.getLanguage()) ? languagePreference.getLanguage() : languageDefault;
        ;
        String[] parts = string.split("-");
        return parts != null && parts.length > 1 ? parts[1] : "AU";
    }

    public Boolean getMask() {
        return false;
    }

    public String getTimeZone() {
        return StringUtil.isNotEmpty(languagePreference.getTimeZone()) ? languagePreference.getTimeZone() : TimeZone.getDefault().getID();
    }

    public String getMessage(final String key) {
        return bundle.getString(key);
    }

    public Long getMaxFileSize() {
        return 104857600L;
    }

}
