package ch.swissbytes.module.shared.rest.configuration;

import java.util.Locale;

/**
 * {@link ThreadLocal} to store the Locale to be used in the message interpolator.
 */
public class LocaleThreadLocal {

    public static final ThreadLocal<Locale> THREAD_LOCAL = new ThreadLocal<Locale>();

    public static Locale get() {
        return (THREAD_LOCAL.get() == null) ? Locale.getDefault() : THREAD_LOCAL.get();
    }

    public static void set(Locale locale) {
        THREAD_LOCAL.set(locale);
    }

    public static void unset() {
        THREAD_LOCAL.remove();
    }
}
