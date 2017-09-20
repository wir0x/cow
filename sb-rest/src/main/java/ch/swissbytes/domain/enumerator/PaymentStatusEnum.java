package ch.swissbytes.domain.enumerator;

import net.minidev.json.JSONObject;

import java.io.Serializable;

public enum PaymentStatusEnum implements Serializable {
    PENDING("PENDING"), PROCESSING("PROCESSING"), PROCESSED("PROCESSED"), ERROR("ERROR");

    private String label;

    PaymentStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public JSONObject getLabelResponse() {
        JSONObject response = new JSONObject();
        String key = "";
        if (label.equals(PENDING.getLabel())) {
            key = "Pendiente";
        } else if (label.equals(PROCESSED.getLabel())) {
            key = "Procesado";
        } else if (label.equals(ERROR.getLabel())) {
            key = "Error";
        }
        response.put("name", key);
        response.put("status", label);
        return response;
    }

    public static PaymentStatusEnum getLabel(String status) {
        switch (status) {
            case "ERROR":
                return ERROR;
            case "PENDING":
                return PENDING;
            case "PROCESSED":
                return PROCESSED;
            default:
                return null;
        }
    }
}
