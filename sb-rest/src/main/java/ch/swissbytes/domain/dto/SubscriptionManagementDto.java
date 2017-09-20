package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionManagementDto implements Serializable {
    private Long subscriptionId;
    private Long deviceId;
    private String imei;
    private boolean battery;
    private boolean sos;
    private String sosMails;
    private String sosCellphones;
    private String batteryMails;
    private String batteryCellphones;
    private String deviceName;
    private String deviceStatus;
    private String deviceIcon;
    private String deviceColor;
    private String deviceType;
    private String subscriptionStatus;
    private String subscriptionInit;
    private String subscriptionEnd;
    private boolean shoppingCart;
    private Long servicePlanId;
    private boolean userPay;
    private boolean hasDropAlarm;
    private String emailsDropAlarm;
    private String cellphonesDropAlarm;

    public static SubscriptionManagementDto createNew() {
        SubscriptionManagementDto dto = new SubscriptionManagementDto();
        dto.subscriptionId = EntityUtil.DEFAULT_LONG;
        dto.deviceId = EntityUtil.DEFAULT_LONG;
        dto.imei = EntityUtil.DEFAULT_STRING;
        dto.battery = EntityUtil.DEFAULT_BOOLEAN;
        dto.sos = EntityUtil.DEFAULT_BOOLEAN;
        dto.sosMails = EntityUtil.DEFAULT_STRING;
        dto.sosCellphones = EntityUtil.DEFAULT_STRING;
        dto.batteryMails = EntityUtil.DEFAULT_STRING;
        dto.batteryCellphones = EntityUtil.DEFAULT_STRING;
        dto.deviceName = EntityUtil.DEFAULT_STRING;
        dto.deviceStatus = EntityUtil.DEFAULT_STRING;
        dto.deviceIcon = EntityUtil.DEFAULT_STRING;
        dto.deviceColor = EntityUtil.DEFAULT_STRING;
        dto.deviceType = EntityUtil.DEFAULT_STRING;
        dto.subscriptionStatus = EntityUtil.DEFAULT_STRING;
        dto.subscriptionInit = EntityUtil.DEFAULT_STRING;
        dto.subscriptionEnd = EntityUtil.DEFAULT_STRING;
        dto.servicePlanId = EntityUtil.DEFAULT_LONG;
        dto.shoppingCart = EntityUtil.DEFAULT_BOOLEAN;
        dto.userPay = EntityUtil.DEFAULT_BOOLEAN;
        dto.hasDropAlarm = EntityUtil.DEFAULT_BOOLEAN;
        dto.emailsDropAlarm = EntityUtil.DEFAULT_STRING;
        dto.cellphonesDropAlarm = EntityUtil.DEFAULT_STRING;
        return dto;
    }

    public static SubscriptionManagementDto fromSubscription(Subscription subscription, boolean onShoppingCart) {
        SubscriptionManagementDto dto = createNew();
        dto.setSubscriptionId(subscription.getId());
        dto.setDeviceId(subscription.getDevice().getId());
        dto.setImei(subscription.getDevice().getImei());
        dto.setSos(subscription.getDevice().getDeviceType().getAlarmSOS());
        dto.setBattery(subscription.getDevice().getDeviceType().getAlarmBattery());
        dto.setSosCellphones(subscription.getDevice().getSosCellphones());
        dto.setBatteryMails(subscription.getDevice().getBatteryMails());
        dto.setBatteryCellphones(subscription.getDevice().getBatteryCellphones());
        dto.setDeviceName(subscription.getDevice().getName());
        dto.setDeviceStatus(subscription.getDevice().getStatus().getLabel());
        dto.setDeviceIcon(subscription.getDevice().getIcon());
        dto.setDeviceColor(subscription.getDevice().getColor());
        dto.setDeviceType(subscription.getDevice().getDeviceType().getDescription());
        dto.setSubscriptionStatus(subscription.getStatus().getLabel());
        dto.setSubscriptionInit(DateUtil.getSimpleDate(subscription.getStartDate()));
        dto.setSubscriptionEnd(DateUtil.getSimpleDate(subscription.getEndDate()));
        dto.setServicePlanId(subscription.getServicePlan() == null ? 0 : subscription.getServicePlan().getId());
        dto.setUserPay(subscription.getUserPay());
        dto.setShoppingCart(onShoppingCart);
        dto.setHasDropAlarm(subscription.getDevice().getDeviceType().getDropWatchAlarm());
        dto.setEmailsDropAlarm(subscription.getDevice().getMailsDropAlarm());
        dto.setCellphonesDropAlarm(subscription.getDevice().getCellphoneDropAlarm());
        return dto;
    }

    public static SubscriptionManagementDto withoutSubscription(Device device) {
        SubscriptionManagementDto dto = createNew();
        dto.setDeviceId(device.getId());
        dto.setImei(device.getImei());
        dto.setSos(device.getDeviceType().getAlarmSOS());
        dto.setBattery(device.getDeviceType().getAlarmBattery());
        dto.setSosCellphones(device.getSosCellphones());
        dto.setBatteryMails(device.getBatteryMails());
        dto.setBatteryCellphones(device.getBatteryCellphones());
        dto.setDeviceName(device.getName());
        dto.setDeviceStatus(device.getStatus().getLabel());
        dto.setDeviceIcon(device.getIcon());
        dto.setDeviceColor(device.getColor());
        dto.setSubscriptionStatus(StatusEnum.DISABLED.getLabel());
        dto.setDeviceType(device.getDeviceType().getDescription());
        dto.setShoppingCart(false);
        dto.setHasDropAlarm(device.getDeviceType().getDropWatchAlarm());
        dto.setEmailsDropAlarm(device.getMailsDropAlarm());
        dto.setCellphonesDropAlarm(device.getCellphoneDropAlarm());
        return dto;
    }
}
