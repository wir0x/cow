package ch.swissbytes.domain.enumerator;

public enum SubscriptionErrorEnum {

    OK(0, "Correcto"),
    ACTIVE(1, "Correcto"),
    NEW_DEVICE(2, "Dispositivo nuevo"),
    EXPIRED(3, "La suscription se encuentra expirada"),
    DEVICE_IN_USE(4, "El dispositivo ya esta siendo utilizado por otro usuario"),
    TM_PENDING(5, "Tigo Money pendiente de aprobación"),
    TM_ERROR(6, "Tigo Money error al realizar el pago"),
    NEW_SMARTPHONE(7, "Dispositivo smartphone no registrado"),
    SERVER_ERROR(8, "Error en la suscripción"),
    NO_SUBSCRIPTION(9, "Dispositivo sin suscripciones activas"),
    DEVICE_PENDING(10, "Dispositivo esta en pendiente para su habilitacion"),
    DEVICE_NOT_LINKED_ANY_ACCOUNT(11, "Dispositivo  no vinculado a ninguna cuenta"),
    PAY_PENDING(12, "En espera de pago"),
    USER_PAY(13, "Usuario debe pagar la suscripcion");

    private final Integer id;
    private final String name;

    SubscriptionErrorEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.name;
    }

    public static SubscriptionErrorEnum getById(Integer id) {
        for (SubscriptionErrorEnum e : values()) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }


}
