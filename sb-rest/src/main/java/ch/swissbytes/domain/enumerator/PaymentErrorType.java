package ch.swissbytes.domain.enumerator;

public enum PaymentErrorType {
    OK(0, "Correcto"),
    NO_FUNDS(1, "Sin saldo en cuenta"),
    TIMEOUT(2, "Tiempo de espera expirado"),
    FAIL(3, "Error desconocido");

    private final Integer id;
    private final String name;

    PaymentErrorType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static PaymentErrorType getById(Integer id) {
        for (PaymentErrorType e : values()) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }
}
