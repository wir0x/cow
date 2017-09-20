package ch.swissbytes.domain.dto;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SmartphoneTrackerDto implements Serializable {
    private String imei;
    private String deviceName;

    public static SmartphoneTrackerDto createNew() {
        SmartphoneTrackerDto subscriptionDto = new SmartphoneTrackerDto();
        subscriptionDto.imei = EntityUtil.DEFAULT_STRING;
        subscriptionDto.deviceName = EntityUtil.DEFAULT_STRING;

        return subscriptionDto;
    }
}
