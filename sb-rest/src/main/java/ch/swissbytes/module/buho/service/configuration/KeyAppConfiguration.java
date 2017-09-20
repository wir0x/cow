package ch.swissbytes.module.buho.service.configuration;

import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class KeyAppConfiguration {

    public static Properties props = new Properties();

    public static String getString(final ConfigurationKey key) {
        return props.getProperty(key.getKey());
    }

    public static URL getUrl(final ConfigurationKey key) {
        URL url = null;
        try {
            url = new URL(props.getProperty(key.getKey()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String get(final ConfigurationKey key) {
        return props.getProperty(key.getKey());
    }

    public static Integer getInt(final ConfigurationKey key) {
        return Integer.parseInt(getString(key));
    }

    public static Long getLong(final ConfigurationKey key) {
        return Long.parseLong(getString(key));
    }

    public static boolean getBoolean(final ConfigurationKey key) {
        return Boolean.valueOf(getString(key));
    }

    public static String[] getArray(final ConfigurationKey key) {
        final String value = getString(key);
        return (value == null ? new String[]{} : value.split(","));
    }

    public static String getStringSValid(final ConfigurationKey key) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Parameter invalid [ " + key.getKey() + " ] ");
        }
        return value;
    }

    public int size() {
        return props.size();
    }

    public String getProperty(final String key) {
        return props.getProperty(key);
    }
}
