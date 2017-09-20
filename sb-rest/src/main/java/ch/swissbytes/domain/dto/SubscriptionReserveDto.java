package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubscriptionReserveDto implements Serializable {

    private Long deviceId;
    private Long planId;
}
