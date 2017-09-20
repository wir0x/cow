package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TrackerDeviceDto implements Serializable {
    private Long deviceId;
    private String generatedId;

    public TrackerDeviceDto(Long deviceId, String generatedId) {
        this.deviceId = deviceId;
        this.generatedId = generatedId;
    }
}
