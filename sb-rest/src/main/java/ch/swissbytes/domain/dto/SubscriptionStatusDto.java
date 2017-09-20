package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class SubscriptionStatusDto implements Serializable {

    private String status;
    private Boolean isLinking;

    public static SubscriptionStatusDto forFreePlan(Subscription subscription) {
        SubscriptionStatusDto subscriptionStatusDto = new SubscriptionStatusDto();
        subscriptionStatusDto.setStatus(StatusEnum.TRIAL_SUBSCRIPTION.getLabel());
        Boolean linking = subscription.getDevice().getAccount() != null;
        subscriptionStatusDto.setIsLinking(linking);
        return subscriptionStatusDto;
    }

    public static SubscriptionStatusDto forEnabledPlan(Subscription subscription) {
        SubscriptionStatusDto subscriptionStatusDto = new SubscriptionStatusDto();
        subscriptionStatusDto.setStatus(StatusEnum.ENABLED.getLabel());
        Boolean linking = subscription.getDevice().getAccount() != null;
        subscriptionStatusDto.setIsLinking(linking);
        return subscriptionStatusDto;
    }

    public static SubscriptionStatusDto forWithOutSubscription(Device device) {
        SubscriptionStatusDto subscriptionStatusDto = new SubscriptionStatusDto();
        subscriptionStatusDto.setStatus(StatusEnum.DISABLED.getLabel());
        Boolean linking = device.getAccount() != null;
        subscriptionStatusDto.setIsLinking(linking);
        return subscriptionStatusDto;
    }
}
