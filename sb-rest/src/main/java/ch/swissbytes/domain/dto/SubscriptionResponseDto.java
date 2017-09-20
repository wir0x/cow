package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionResponseDto implements Serializable {

    private Long id;                // Id subscription
    private String from;            // Start date subscription.
    private String to;              // End date subscription.
    private Integer maxSms;         // Max sms per month
    private String status;          // Status subscription
    private Long servicePlanId;
    private String servicePlanName;
    private DeviceDto device;

    public static SubscriptionResponseDto createNew() {
        SubscriptionResponseDto subscriptionDto = new SubscriptionResponseDto();
        subscriptionDto.id = EntityUtil.DEFAULT_LONG;
        subscriptionDto.from = EntityUtil.DEFAULT_STRING;
        subscriptionDto.to = EntityUtil.DEFAULT_STRING;
        subscriptionDto.status = EntityUtil.DEFAULT_STRING;
        subscriptionDto.maxSms = EntityUtil.DEFAULT_INTEGER;
        subscriptionDto.servicePlanId = EntityUtil.DEFAULT_LONG;
        subscriptionDto.servicePlanName = EntityUtil.DEFAULT_STRING;
        subscriptionDto.device = null;
        return subscriptionDto;
    }

    public static SubscriptionResponseDto withoutSubscription(Device device) {
        SubscriptionResponseDto subscriptionDto = new SubscriptionResponseDto();
        subscriptionDto.id = EntityUtil.DEFAULT_LONG;
        subscriptionDto.from = EntityUtil.DEFAULT_STRING;
        subscriptionDto.to = EntityUtil.DEFAULT_STRING;
        subscriptionDto.status = EntityUtil.DEFAULT_STRING;
        subscriptionDto.maxSms = EntityUtil.DEFAULT_INTEGER;
        subscriptionDto.servicePlanId = EntityUtil.DEFAULT_LONG;
        subscriptionDto.servicePlanName = EntityUtil.DEFAULT_STRING;
        subscriptionDto.device = DeviceDto.from(device);
        return subscriptionDto;
    }

    public static SubscriptionResponseDto fromEntity(Subscription subscription) {
        SubscriptionResponseDto subscriptionDto = createNew();
        subscriptionDto.id = subscription.getId();
        subscriptionDto.from = DateUtil.getSimpleDate(subscription.getStartDate());
        subscriptionDto.to = DateUtil.getSimpleDate(subscription.getEndDate());
        subscriptionDto.status = subscription.getStatus().getLabel();
        subscriptionDto.maxSms = subscription.getMaxSms();
        subscriptionDto.servicePlanId = subscription.getServicePlan().getId();
        subscriptionDto.servicePlanName = subscription.getServicePlan().getName();
        subscriptionDto.device = DeviceDto.from(subscription.getDevice());
        return subscriptionDto;
    }
}
