package ch.swissbytes.module.shared.rest.configuration;

import javax.validation.MessageInterpolator;
import java.util.Locale;

/**
 * Delegates to a MessageInterpolator implementation but enforces a given Locale.
 */
public class LocaleSpecificMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator defaultInterpolator;

    public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator) {
        this.defaultInterpolator = interpolator;
    }

    @Override
    public String interpolate(String message, Context context) {
        return defaultInterpolator.interpolate(message, context, LocaleThreadLocal.get());
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return defaultInterpolator.interpolate(message, context, locale);
    }
}
