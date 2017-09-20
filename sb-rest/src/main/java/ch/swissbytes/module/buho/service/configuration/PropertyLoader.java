package ch.swissbytes.module.buho.service.configuration;

import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.buho.app.configuration.model.Configuration;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Startup
@Singleton
public class PropertyLoader extends Repository {

    @PostConstruct
    public void init() {
        Map<String, String> properties = fetchAllAppProperties();
        loadIntoSystemProperties(properties);
    }

    private Map<String, String> fetchAllAppProperties() {

        List<Configuration> configurationList = findAll(Configuration.class);
        Map<String, String> properties = new HashMap<>();

        for (Configuration configuration : configurationList) {
            properties.put(configuration.getKey(), configuration.getValue());
        }

        return properties;
    }

    private void loadIntoSystemProperties(Map<String, String> properties) {
        KeyAppConfiguration.props.putAll(properties);
    }
}
