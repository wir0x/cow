package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SubscriptionPayTMDto implements Serializable {

    private String tigoPhoneNumber;
    private String tigoIdCardNumber;
    private Long nit;
    private String businessName;
    private List<SubscriptionPay> subscriptionPayList;
}
