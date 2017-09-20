package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum ApplicationTypeEnum implements Serializable {
    VIEWER("VIEWER"), TRACKER("TRACKER"), WEB("WEB");

    private String label;

    ApplicationTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
