package ch.swissbytes.module.buho.service.configuration;

public enum ConfigurationKey {

    URL_POSITION_SERVER("url.positionserver"),
    PORT_GV300("port.gv300"),
    DATE_FORMAT_SHORT("date.format.short"),
    SMTP_SERVER("smtp.server"),
    SMTP_PORT("smtp.port"),
    SMTP_USERNAME("smtp.username"),
    SMTP_PASSWORD("smtp.password"),
    SMTP_SENDER_ADDRESS("smtp.sender.address"),
    SMTP_SENDER_NAME("smtp.sender.name"),
    SMTP_SSL("smtp.ssl"),
    SMS_URL("sms.url"),
    SMS_TOKEN("sms.token"),
    SMS_SENDER("sms.sender"),
    COMPANY_TEST_ID("company.test.id"),
    SMTP_SENDER_ADDRESS_SUPPORT("smtp.sender.address.support"),
    SMS_MAX_LIMIT("sms.max.limit"),
    SMS_LIMIT_PERCENTAGE("default.sms.limit"),
    TIME_IDLE_TIME("default.idle.time"),
    LOW_BATTERY_LEVEL_1("low.battery.level.1"),
    LOW_BATTERY_LEVEL_2("low.battery.level.2"),
    TIME_LOW_BATTERY_LIMIT("time.low.battery.limit"),
    GL200_DEVICE_TYPE_ID("gl200.device.type.id"),
    MONTH_NUMBER_TO_SHOW("month.number.to.show"),
    REMAINING_DAYS_SUBSCRIPTION_1("remaining.days.subscription.1"),
    REMAINING_DAYS_SUBSCRIPTION_2("remaining.days.subscription.2"),
    LIMIT_SPEED("limit.speed"),
    LIMIT_SPEED_POSITION("limit.position.speed"),
    LIMIT_DISTANCE("limit.distance"),
    DEFAULT_EXPIRED_TIME("default.expired.time"),
    ADDITIONAL_EXPIRED_TIME("additional.expired.time"),
    MOBILE_OS_KEY("mobile.os.key"),
    MAX_DIGIT_TRACKER_ID("max.digit.tracker.id"),
    MIN_DIGIT_TRACKER_ID("min.digit.tracker.id"),
    TIME_SOS_INTERVAL("time.sos.interval"),
    URLS_NO_EXTEND_SESSION_TIME("urls.no.extend.session.time"),
    GCM_URL("gcm.url"),
    AUTHORIZATION_TOKEN("auth.token"),
    SMARTPHONE_TYPE_ID("smartphone.device.type.id"),
    ROLE_TRACKER_ID("role.tracker.id"),
    ROLE_USER_ID("role.user.id"),
    ROLE_ADMIN_ID("role.admin.id"),
    DIGIT_NUMBER_PASSWORD("digit.number.password"),
    DEFAULT_PERMISSIONS_WITHOUT_DEVICE("default.permissions.without.device"),
    FREE_PLAN_ID("free.service.plan.id"),
    PREFIX_PHONE_NUMBERS_TIGO("prefix.phone.numbers.tigo"),
    SELLER_USERS("seller.user"),
    SYSTEM_ADMIN_GROUP_ID("system.administrator.group.id"),
    USERNAME_BUHO_SYS_ADMIN("username.buho.sys.admin"),
    USERNAME_UBIDATA_SYS_ADMIN("username.ubidata.sys.admin");

    private final String key;

    private ConfigurationKey(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
