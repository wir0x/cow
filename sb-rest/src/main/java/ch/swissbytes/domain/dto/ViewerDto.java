package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter
@Setter
@ToString
public class ViewerDto extends PushNotificationDto {

    private String deviceId;
    private String fenceId;
    private String userId;
    private String deviceIcon;
    private String deviceName;
    private String fenceName;
    private String phoneNumber;
    private String deviceType;
    private String latitude;
    private String longitude;
    private Date dateTime;

    public static ViewerDto createNew() {
        ViewerDto viewerDto = new ViewerDto();
        viewerDto.userId = EntityUtil.DEFAULT_STRING;
        viewerDto.deviceId = EntityUtil.DEFAULT_STRING;
        viewerDto.fenceId = EntityUtil.DEFAULT_STRING;
        viewerDto.deviceIcon = EntityUtil.DEFAULT_STRING;
        viewerDto.deviceName = EntityUtil.DEFAULT_STRING;
        viewerDto.fenceName = EntityUtil.DEFAULT_STRING;
        viewerDto.phoneNumber = EntityUtil.DEFAULT_STRING;
        viewerDto.deviceType = EntityUtil.DEFAULT_STRING;
        viewerDto.latitude = EntityUtil.DEFAULT_STRING;
        viewerDto.longitude = EntityUtil.DEFAULT_STRING;
        viewerDto.dateTime = EntityUtil.DEFAULT_DATE;
        return viewerDto;
    }

    public static ViewerDto from(Device device, Alarm alarm) {
        ViewerDto viewerDto = createNew();
        viewerDto.setUserId(device.getAccount().getId().toString());
        viewerDto.setDeviceId(device.getId().toString());
        viewerDto.setDeviceIcon(device.getIcon());
        viewerDto.setDeviceName(device.getName());
        viewerDto.setPhoneNumber(device.getPhoneNumber());
        viewerDto.setDeviceType(device.getDeviceType().getId().toString());
        viewerDto.setLatitude(alarm.getLatitude().toString());
        viewerDto.setLongitude(alarm.getLongitude().toString());
        viewerDto.setDateTime(alarm.getDateReceived());
        return viewerDto;
    }

    public static ViewerDto from(Position position) {
        ViewerDto viewerDto = createNew();
        viewerDto.setUserId(position.getDevice().getAccount() != null ? position.getDevice().getAccount().getId().toString() : "0");
        viewerDto.setDeviceId(position.getDevice().getId().toString());
        viewerDto.setDeviceIcon(position.getDevice().getIcon());
        viewerDto.setDeviceName(position.getDevice().getName());
        viewerDto.setPhoneNumber(position.getDevice().getPhoneNumber());
        viewerDto.setDeviceType(position.getDevice().getDeviceType().getId().toString());
        viewerDto.setLatitude(position.getLatitude().toString());
        viewerDto.setLongitude(position.getLongitude().toString());
        viewerDto.setDateTime(position.getTime());
        return viewerDto;
    }
}
