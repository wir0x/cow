package ch.swissbytes.domain.enumerator;

import java.io.Serializable;

public enum NotificationTypeEnum implements Serializable {
    EMAIL("EMAIL"), SMS("SMS"), WHATSAPP("WHATSAPP"), PUSH_NOTIFICATION("PUSH_NOTIFICATION");

    private String label;

    NotificationTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
