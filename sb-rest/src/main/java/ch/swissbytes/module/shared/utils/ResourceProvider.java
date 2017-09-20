package ch.swissbytes.module.shared.utils;

import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides access to service.properties file parameters and WildFly's system properties
 */
public class ResourceProvider {

    private static final Logger log = LoggerFactory.logger(ResourceProvider.class);

    private static final Locale locale = new Locale("es", "ES");

    private static final ResourceBundle labels = ResourceBundle.getBundle("messages", locale);

    private static final String RESOURCE_BUNDLE_NAME = "config";
    @Inject
    ConfigurationRepository configurationRepository;

    private ResourceProvider() {
    }

    public static String get(String key) {
        String value = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME).getString(key);
        log.debug(String.format("Reading resource from %s.properties - %s = %s", RESOURCE_BUNDLE_NAME, key, value));
        return value;
    }
}
