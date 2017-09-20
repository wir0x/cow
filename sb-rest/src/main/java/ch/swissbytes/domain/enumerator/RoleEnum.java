package ch.swissbytes.domain.enumerator;

public enum RoleEnum {
    SYSTEM_ADMIN(1), ACCOUNT_ADMIN(2), ADMIN(3), USER(4);

    private long id;

    RoleEnum(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
