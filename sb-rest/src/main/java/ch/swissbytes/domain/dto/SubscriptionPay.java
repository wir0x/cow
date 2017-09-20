package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionPay implements Serializable {

    private Long deviceId;
    private Long subscriptionId;
    private Long servicePlanId;
}
