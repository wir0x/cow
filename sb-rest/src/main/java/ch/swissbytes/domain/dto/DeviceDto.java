package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class DeviceDto implements Serializable {

    private Long id;
    private String imei;
    private String name;
    private String icon;
    private String color;
    private String sosMails;
    private String sosCellphones;
    private String batteryMails;
    private String batteryCellphones;
    private boolean sos;
    private boolean battery;
    private boolean hasDropAlarm;
    private String deviceType;
    private String cellphonesDropAlarm;
    private String emailsDropAlarm;

    public static DeviceDto createNew() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.id = EntityUtil.DEFAULT_LONG;
        deviceDto.imei = EntityUtil.DEFAULT_STRING;
        deviceDto.name = EntityUtil.DEFAULT_STRING;
        deviceDto.icon = EntityUtil.DEFAULT_STRING;
        deviceDto.color = EntityUtil.DEFAULT_STRING;
        deviceDto.battery = EntityUtil.DEFAULT_BOOLEAN;
        deviceDto.sos = EntityUtil.DEFAULT_BOOLEAN;
        deviceDto.sosMails = EntityUtil.DEFAULT_STRING;
        deviceDto.sosCellphones = EntityUtil.DEFAULT_STRING;
        deviceDto.batteryMails = EntityUtil.DEFAULT_STRING;
        deviceDto.sosCellphones = EntityUtil.DEFAULT_STRING;
        deviceDto.hasDropAlarm = EntityUtil.DEFAULT_BOOLEAN;
        deviceDto.cellphonesDropAlarm = EntityUtil.DEFAULT_STRING;
        deviceDto.emailsDropAlarm = EntityUtil.DEFAULT_STRING;
        return deviceDto;
    }

    public static DeviceDto from(Device device) {
        DeviceDto dto = DeviceDto.createNew();
        dto.id = device.getId();
        dto.imei = device.getImei();
        dto.name = device.getName();
        dto.icon = device.getIcon();
        dto.color = device.getColor();
        dto.battery = device.getDeviceType().getAlarmBattery();
        dto.sos = device.getDeviceType().getAlarmSOS();
        dto.sosMails = device.getSosMails();
        dto.sosCellphones = device.getSosCellphones();
        dto.batteryMails = device.getBatteryMails();
        dto.batteryCellphones = device.getBatteryCellphones();
        dto.deviceType = device.getDeviceType().getDescription();
        return dto;
    }

    public static List<DeviceDto> fromDeviceList(List<Device> deviceList) {
        return deviceList.stream().map(DeviceDto::from).collect(Collectors.toList());
    }

    public Device convertToDevice(Optional<Device> deviceOptional) {
        Device device = deviceOptional.isPresent() ? deviceOptional.get() : Device.createNew();
        device.setId(getId());
        device.setImei(getImei());
        device.setName(getName());
        device.setIcon(getIcon());
        device.setColor(getColor());
        device.setSosMails(getSosMails());
        device.setSosCellphones(getSosCellphones());
        device.setBatteryMails(getBatteryMails());
        device.setBatteryCellphones(getBatteryCellphones());
        device.setCellphoneDropAlarm(getCellphonesDropAlarm());
        device.setMailsDropAlarm(getEmailsDropAlarm());
        return device;
    }
}
