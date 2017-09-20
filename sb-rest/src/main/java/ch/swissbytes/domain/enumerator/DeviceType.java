package ch.swissbytes.domain.enumerator;

public enum DeviceType {

    GL200(1, "GL200", true, true),
    GV300(2, "GV300", false, false);

    private final Integer id;
    private final String name;
    private final Boolean hasAlarm;
    private final Boolean hasBattery;

    DeviceType(Integer id, String name, Boolean hasAlarm, Boolean hasBattery) {
        this.id = id;
        this.name = name;
        this.hasAlarm = hasAlarm;
        this.hasBattery = hasBattery;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Boolean isHasAlarm() {
        return hasAlarm;
    }

    public Boolean isHasBattery() {
        return hasBattery;
    }

    public static DeviceType getById(Integer id) {
        for (DeviceType e : values()) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }
}
