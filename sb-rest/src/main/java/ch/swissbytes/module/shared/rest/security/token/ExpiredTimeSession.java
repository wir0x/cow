package ch.swissbytes.module.shared.rest.security.token;

import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;

import java.io.Serializable;
import java.util.Date;

public class ExpiredTimeSession implements Serializable {

    private int expiredSessionTime = getDefaultSessionTime();

    private Date expirationTime = new Date(new Date().getTime() + 1000 * 60 * expiredSessionTime);

    private int getDefaultSessionTime() {
        return KeyAppConfiguration.getInt(ConfigurationKey.DEFAULT_EXPIRED_TIME);
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
