package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum StatusEnum implements Serializable {
    ENABLED("ENABLED"),
    DISABLED("DISABLED"),
    DELETED("DELETED"),
    PENDING("PENDING"),
    EXPIRED("EXPIRED"),
    TRIAL_SUBSCRIPTION("TRIAL_SUBSCRIPTION");

    private String label;

    StatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
