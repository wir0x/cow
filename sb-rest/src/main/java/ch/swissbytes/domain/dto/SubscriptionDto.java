package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.ServicePlan;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SubscriptionDto implements Serializable {

    private Long id;
    private String deviceName;
    private String accountName;
    private Long deviceId;
    private Integer maxSms;
    private String startDate;
    private String endDate;
    private String status;
    private Long servicePlanId;
    private String servicePlanName;
    private List<SmsControlDto> smsControlDtoList;

    public static SubscriptionDto createNew() {
        SubscriptionDto dto = new SubscriptionDto();
        dto.id = EntityUtil.DEFAULT_LONG;
        dto.startDate = EntityUtil.DEFAULT_STRING;
        dto.endDate = EntityUtil.DEFAULT_STRING;
        dto.status = StatusEnum.DISABLED.getLabel();
        dto.maxSms = EntityUtil.DEFAULT_INTEGER;
        dto.deviceId = EntityUtil.DEFAULT_LONG;
        dto.deviceName = EntityUtil.DEFAULT_STRING;
        dto.servicePlanId = EntityUtil.DEFAULT_LONG;
        dto.servicePlanName = EntityUtil.DEFAULT_STRING;
        dto.accountName = EntityUtil.DEFAULT_STRING;
        dto.smsControlDtoList = null;
        return dto;
    }

    public static SubscriptionDto newWithoutSubscription() {
        SubscriptionDto dto = createNew();
        dto.status = StatusEnum.DISABLED.getLabel();
        return dto;
    }

    public static SubscriptionDto fromSubscriptionEntity(Subscription subscription, Device device, Account account) {
        SubscriptionDto dto = createNew();
        dto.id = subscription.getId();
        dto.startDate = DateUtil.getSimpleDate(subscription.getStartDate());
        dto.endDate = DateUtil.getSimpleDate(subscription.getEndDate());
        dto.maxSms = subscription.getMaxSms();
        dto.deviceId = device.getId();
        dto.deviceName = device.getName();
        dto.servicePlanId = subscription.getServicePlan() == null ? 0 : subscription.getServicePlan().getId();
        dto.servicePlanName = subscription.getServicePlan() == null ? "" : subscription.getServicePlan().getName();
        dto.smsControlDtoList = null;
        dto.accountName = account != null ? account.getName() : null;
        return dto;
    }

    public static SubscriptionDto fromSubscriptionEntity(Subscription subscription) {
        SubscriptionDto dto = createNew();
        dto.id = subscription.getId();
        dto.startDate = DateUtil.getSimpleDate(subscription.getStartDate());
        dto.endDate = DateUtil.getSimpleDate(subscription.getEndDate());
        dto.status = subscription.getStatus().getLabel();
        dto.maxSms = subscription.getMaxSms();
        dto.deviceId = subscription.getDevice().getId();
        dto.deviceName = subscription.getDevice().getName();
        dto.servicePlanId = subscription.getServicePlan() == null ? 0 : subscription.getServicePlan().getId();
        dto.servicePlanName = subscription.getServicePlan() == null ? "" : subscription.getServicePlan().getName();
        dto.smsControlDtoList = null;
        dto.accountName = subscription.getDevice().getAccount() != null ? subscription.getDevice().getAccount().getName() : null;
        return dto;
    }

    public static SubscriptionDto fromEntity(Subscription subscription) {
        SubscriptionDto dto = createNew();
        dto.id = subscription.getId();
        dto.startDate = DateUtil.getSimpleDate(subscription.getStartDate());
        dto.endDate = DateUtil.getSimpleDate(subscription.getEndDate());
        dto.maxSms = subscription.getMaxSms();
        dto.deviceId = subscription.getDevice().getId();
        dto.deviceName = subscription.getDevice().getName();
        dto.servicePlanId = subscription.getServicePlan() != null ? subscription.getServicePlan().getId() : null;
        dto.servicePlanName = subscription.getServicePlan() != null ? subscription.getServicePlan().getName() : null;
        dto.smsControlDtoList = null;
        dto.accountName = subscription.getDevice().getAccount() != null ? subscription.getDevice().getAccount().getName() : null;
        return dto;
    }

    public static Subscription fromSubscriptionDto(SubscriptionDto subscriptionDto) {
        Subscription subscription = Subscription.createNew();
        subscription.setId(subscriptionDto.getId());
        subscription.setDevice(new Device(subscriptionDto.getDeviceId()));
        subscription.setUserPay(false);
        subscription.setStatus(StatusEnum.ENABLED);
        subscription.setMaxSms(subscriptionDto.getMaxSms());
        subscription.setStartDate(DateUtil.getDateFromString(subscriptionDto.getStartDate()));
        subscription.setEndDate(DateUtil.getDateFromString(subscriptionDto.getEndDate()));
        subscription.setServicePlan(new ServicePlan(subscriptionDto.getServicePlanId()));
        return subscription;
    }

    public static List<SubscriptionDto> fromSubscriptionList(List<Subscription> subscriptionList) {
        List<SubscriptionDto> subscriptionDtoList = new ArrayList<>();

        for (Subscription subscription : subscriptionList) {
            SubscriptionDto subscriptionDto = SubscriptionDto.fromSubscriptionEntity(subscription
                    , subscription.getDevice()
                    , subscription.getDevice().getAccount());
            subscriptionDtoList.add(subscriptionDto);
        }
        return subscriptionDtoList;
    }
}
