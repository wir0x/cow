package ch.swissbytes.module.shared.utils;

public enum AlarmType {
    MT58_DROP_OFF("DROP"),
    GL200_LOW_BATTERY("GTBPL"),
    GV300_CONNECT_POWER_SUPPLY("GTMPN"),
    GV300_DISCONNECT_POWER_SUPPLY("GTMPF"),
    GL200_GV300_SOS("GTSOS");

    private String label;

    AlarmType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
