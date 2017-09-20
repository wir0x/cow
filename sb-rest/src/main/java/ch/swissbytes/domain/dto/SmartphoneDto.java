package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SmartphoneDto implements Serializable {

    private String imei;
    private String uuid;
    private String gcmToken;

}
