package ch.swissbytes.module.buho.rest.v1.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter @Setter @ToString
public class DeviceDto implements Serializable{

    private Long id;
    private String name;
    private String icon;
    private String color;
    private String type;

    public static DeviceDto from(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.id = device.getId();
        dto.name = device.getName();
        dto.icon = device.getIcon();
        dto.color = device.getColor();
        dto.type = device.getDeviceType().getDescription();
        return dto;
    }
}
