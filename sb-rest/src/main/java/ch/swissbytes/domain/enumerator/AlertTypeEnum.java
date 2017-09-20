package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum AlertTypeEnum implements Serializable {
    FENCE("FENCE"),
    SOS("SOS"),
    BATTERY("BATTERY"),
    LOW_BATTERY("LOW_BATTERY"),
    SUBSCRIPTION("SUBSCRIPTION");

    private String label;

    AlertTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
