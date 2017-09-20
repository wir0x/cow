package ch.swissbytes.module.buho.app.configuration.repository;

import ch.swissbytes.module.buho.app.configuration.model.Configuration;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ConfigurationRepository extends Repository {

    private static final String URL_DOMAIN = "url.domain";

    private static final String URL_FRONTEND = "url.frontend";

    private static final String PAGE_RESET_PASSWORD = "page.reset.password";

    private static final String COMPANY_TEST_ID = "company.test.id";

    private static final String DEFAULT_USER_GROUP_ID = "default.app.user.group.id";

    private static final String FREE_SERVICE_PLAN_ID = "free.service.plan.id";

    private static final String SMARTPHONE_TYPE_ID = "smartphone.device.type.id";

    private static final String TIGO_MONEY_ENCRYPT_KEY = "tm.encrypt.key";

    private static final String TIGO_MONEY_ID_KEY = "tm.id.key";

        private static final String TIGO_MONEY_URL_WEBSERVICE = "tm.url.webservice";

    private static final String APP_ACCOUNT_ID = "app.account.id";

    @Inject
    private Logger log;

    public List<Configuration> findAll() {
        return findAll(Configuration.class);
    }

    public Optional<Configuration> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Configuration.class, id);
    }

    public Optional<Configuration> getByKey(final String key) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("key", key);
        return StringUtil.isEmpty(key) ? Optional.absent() : getBy(Configuration.class, filters);
    }

    public String get(String key) {
        Optional<Configuration> configurationOptional = getByKey(key);
        if (configurationOptional.isAbsent()) {
            log.error("Value not found for key: " + key);
            return "";
        }
        String value = configurationOptional.get().getValue();
        log.info("value:" + value);
        return value;
    }

    public String getUrlDomain() {
        return get(URL_DOMAIN);
    }

    public String getUrlResetPassword() {
        return getUrlDomain() + get(URL_FRONTEND) + get(PAGE_RESET_PASSWORD);
    }

    public Long getCompanyTestId() {
        return Long.valueOf(get(COMPANY_TEST_ID));
    }

    public Long getDefaultAppUserGroupId() {
        return Long.valueOf(get(DEFAULT_USER_GROUP_ID));
    }

    public Long getFreeServicePlanId() {
        return Long.valueOf(get(FREE_SERVICE_PLAN_ID));
    }

    public Long getSmartphoneTypeId() {
        return Long.valueOf(get(SMARTPHONE_TYPE_ID));
    }

    public String getTigoMoneyEncryptKey() {
        return get(TIGO_MONEY_ENCRYPT_KEY);
    }

    public String getTigoMoneyIdKey() {
        return get(TIGO_MONEY_ID_KEY);
    }

    public String getTigoMoneyUrlWebservice() {
        return get(TIGO_MONEY_URL_WEBSERVICE);
    }

    public Long getAppAccountId() {
        return Long.valueOf(get(APP_ACCOUNT_ID));
    }

}
