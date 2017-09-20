package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class PositionDto implements Serializable {

    private int id;
    private int index;
    private String latitude;
    private String longitude;
    private String speed;
    private String battery;
    private String date;
    private String time;
    private Boolean actual;
    private Long deviceId;
    private String name;
    private String icon;
    private String color;
    private String maxSpeed;
    private String avgSpeed;
    private String idleTime;
    private String distance;
    private String phoneNumber;
    private Long deviceType;
    private SubscriptionDto subscription;


    public static PositionDto createNew() {
        PositionDto dto = new PositionDto();
        dto.id = EntityUtil.DEFAULT_INTEGER;
        dto.index = EntityUtil.DEFAULT_INTEGER;
        dto.latitude = EntityUtil.DEFAULT_STRING;
        dto.longitude = EntityUtil.DEFAULT_STRING;
        dto.speed = EntityUtil.DEFAULT_STRING;
        dto.battery = EntityUtil.DEFAULT_STRING;
        dto.date = EntityUtil.DEFAULT_STRING;
        dto.time = EntityUtil.DEFAULT_STRING;
        dto.actual = EntityUtil.DEFAULT_BOOLEAN;
        dto.deviceId = EntityUtil.DEFAULT_LONG;
        dto.name = EntityUtil.DEFAULT_STRING;
        dto.icon = EntityUtil.DEFAULT_STRING;
        dto.color = EntityUtil.DEFAULT_STRING;
        dto.maxSpeed = EntityUtil.DEFAULT_STRING;
        dto.avgSpeed = EntityUtil.DEFAULT_STRING;
        dto.idleTime = EntityUtil.DEFAULT_STRING;
        dto.distance = EntityUtil.DEFAULT_STRING;
        dto.phoneNumber = EntityUtil.DEFAULT_STRING;
        dto.deviceType = EntityUtil.DEFAULT_LONG;
        return dto;
    }

    public static PositionDto fromPosition(Position position, Device device, int index) {
        PositionDto dto = PositionDto.createNew();
        dto.id = index;
        dto.index = index;
        dto.latitude = String.valueOf(position.getLatitude());
        dto.longitude = String.valueOf(position.getLongitude());
        dto.speed = String.valueOf(LongUtil.decimalFormat(position.getSpeed(), 2));
        dto.battery = device.getDeviceType().getBattery() ? position.getBattery() : null;
        dto.date = String.valueOf(DateUtil.getSimpleDate(position.getTime()));
        dto.time = String.valueOf(DateUtil.getSimpleTime(position.getTime()));
        dto.actual = position.getActual();
        dto.deviceId = device.getId();
        dto.name = device.getName();
        dto.icon = device.getIcon();
        dto.color = device.getColor();
        dto.maxSpeed = position.getMaxSpeed() != null ? String.valueOf(LongUtil.decimalFormat(position.getMaxSpeed(), 2)) : "";
        dto.avgSpeed = position.getAvgSpeed() != null ? String.valueOf(LongUtil.decimalFormat(position.getAvgSpeed(), 2)) : "";
        dto.distance = position.getDistance() != null ? String.valueOf(LongUtil.decimalFormat(position.getDistance(), 2)) : "";
        dto.phoneNumber = device.getPhoneNumber() != null ? device.getPhoneNumber() : "";
        dto.deviceType = device.getDeviceType().getId();
        return dto;
    }

    public static PositionDto from(Position position, Subscription subscription, int index) {
        PositionDto dto = createNew();
        // position
        dto.id = index;
        dto.index = index;
        dto.latitude = String.valueOf(position.getLatitude());
        dto.longitude = String.valueOf(position.getLongitude());
        dto.speed = String.valueOf(LongUtil.decimalFormat(position.getSpeed(), 2));
        dto.battery = position.getDevice().getDeviceType().getBattery() ? position.getBattery() : null;
        dto.date = String.valueOf(DateUtil.getSimpleDate(position.getTime()));
        dto.time = String.valueOf(DateUtil.getSimpleTime(position.getTime()));
        dto.actual = isPositionActual(position);
        // device
        dto.deviceId = position.getDevice().getId();
        dto.name = position.getDevice().getName();
        dto.icon = position.getDevice().getIcon();
        dto.color = position.getDevice().getColor();
        dto.deviceType = position.getDevice().getDeviceType().getId();
        dto.phoneNumber = position.getDevice().getPhoneNumber();
        // subscription
        dto.subscription = SubscriptionDto.fromSubscriptionEntity(subscription, position.getDevice(), position.getDevice().getAccount());
        return dto;
    }

    private static boolean isPositionActual(Position position) {
        return (position.getTime().getTime() + 90000) >= (new Date().getTime());
    }
}
