package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class DeviceBackendDto implements Serializable {

    private Long id;
    private String imei;
    private String phoneNumber;
    private String name;
    private Long accountId;
    private String accountName;
    private Boolean status;
    private Long deviceTypeId;
    private String nameDeviceType;
    private Boolean hasBattery;
    private Boolean sosAlarm;
    private Boolean batteryAlarm;

    public static DeviceBackendDto createNew() {
        DeviceBackendDto deviceBackendDto = new DeviceBackendDto();
        deviceBackendDto.id = EntityUtil.DEFAULT_LONG;
        deviceBackendDto.imei = EntityUtil.DEFAULT_STRING;
        deviceBackendDto.name = EntityUtil.DEFAULT_STRING;
        deviceBackendDto.phoneNumber = EntityUtil.DEFAULT_STRING;
        deviceBackendDto.accountId = EntityUtil.DEFAULT_LONG;
        deviceBackendDto.status = EntityUtil.DEFAULT_BOOLEAN;
        deviceBackendDto.accountName = EntityUtil.DEFAULT_STRING;
        deviceBackendDto.deviceTypeId = EntityUtil.DEFAULT_LONG;
        deviceBackendDto.nameDeviceType = EntityUtil.DEFAULT_STRING;
        deviceBackendDto.hasBattery = EntityUtil.DEFAULT_BOOLEAN;
        deviceBackendDto.sosAlarm = EntityUtil.DEFAULT_BOOLEAN;
        deviceBackendDto.batteryAlarm = EntityUtil.DEFAULT_BOOLEAN;
        return deviceBackendDto;
    }

    public static DeviceBackendDto fromDeviceEntity(Device device) {
        DeviceBackendDto deviceBackendDto = DeviceBackendDto.createNew();
        deviceBackendDto.id = device.getId();
        deviceBackendDto.imei = device.getImei();
        deviceBackendDto.name = device.getName();
        deviceBackendDto.status = device.getStatus().equals(StatusEnum.ENABLED);
        deviceBackendDto.phoneNumber = device.getPhoneNumber();
        deviceBackendDto.accountId = device.getAccount() != null ? device.getAccount().getId() : null;
        deviceBackendDto.accountName = device.getAccount() != null ? device.getAccount().getName() : null;
        deviceBackendDto.sosAlarm = device.getDeviceType().getAlarmSOS() != null ? device.getDeviceType().getAlarmSOS() : false;
        deviceBackendDto.batteryAlarm = device.getDeviceType().getAlarmBattery() != null ? device.getDeviceType().getAlarmBattery() : false;
        deviceBackendDto.nameDeviceType = device.getDeviceType().getDescription();
        deviceBackendDto.deviceTypeId = device.getDeviceType().getId();
        deviceBackendDto.hasBattery = device.getDeviceType().getBattery();
        return deviceBackendDto;
    }

    public Device deviceEntityFromDeviceDtoBackend(Optional<Device> deviceOptional) {
        Device device = deviceOptional.isPresent() ? deviceOptional.get() : Device.createNew();
        device.setName(getName());
        device.setImei(getImei());
        device.setPhoneNumber(getPhoneNumber());
        device.setStatus(StatusEnum.ENABLED);
        device.setIcon(device.isNew() ? "fa-map-marker" : device.getIcon());
        device.setColor(device.isNew() ? "dd4b39" : device.getColor());
        device.setDeviceType(new DeviceType(getDeviceTypeId()));
        device.setAccount(LongUtil.isEmpty(getAccountId()) ? null : new Account(getAccountId()));
        return device;
    }

    public Device toDevice(Optional<Device> deviceOptional) {
        Device device = deviceOptional.isPresent() ? deviceOptional.get() : Device.createNew();
        device.setId(getId());
        device.setImei(getImei());
        device.setPhoneNumber(getPhoneNumber());
        device.setAccount(new Account(accountId));
        return device;
    }
}
