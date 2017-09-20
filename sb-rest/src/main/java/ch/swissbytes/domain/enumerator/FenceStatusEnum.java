package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum FenceStatusEnum implements Serializable {
    ENTER("ENTER"), LEFT("LEFT"), NONE("NONE");

    private String label;

    FenceStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}

