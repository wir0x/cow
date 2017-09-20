package ch.swissbytes.domain.enumerator;

public enum PushNotificationType {
    PAYMENT_RESPONSE(1, "Respuesta del pago"),
    SETTINGS_UPDATE(2, "Actualización de configuraciones de la aplicación"),
    SUBSCRIPTION_WARNING(3, "Advertencia sobre la suscripción"),
    PROMO(4, "Mensaje genérico"),
    ALERT_SOS(5, "Buho SOS"),
    ALERT_LOW_BATTERY(6, "Buho Batteria baja"),
    ALERT_FENCE(7, "Buho Fence"),
    ALERT_SUBSCRIPTION(8, "Buho Suscripcion"),
    ALERT_DISCONNECT_BATTERY(9, "Buho Bateria desconectada ");


    private final Integer id;
    private final String name;

    PushNotificationType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static PushNotificationType getById(Integer id) {
        for (PushNotificationType e : values()) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }
}
