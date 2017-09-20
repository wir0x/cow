package ch.swissbytes.domain.enumerator;

public enum RespErrorEnum {

    OK(0, "Correcto"),
    REGISTER_ERROR(1, "Error al registrar"),
    DUPLICATE_ERROR(2, "Registro duplicado"),
    DATA_ERROR(3, "Datos incorrectos"),
    FORBIDDEN_ERROR(4, "Acceso no válido"),
    TRANSACTION_ERROR(5, "Operación no permitida"),
    SERVICE_ERROR(6, "Error de servicio");

    private final Integer id;
    private final String name;

    RespErrorEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static RespErrorEnum getById(Integer id) {
        for (RespErrorEnum e : values()) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }
}
