package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum StatusDeviceEnum implements Serializable {
    FREE("FREE"), ASSIGNED("ASSIGNED"), WRONG("WRONG"), ENABLED("ENABLED");

    private String label;

    StatusDeviceEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
