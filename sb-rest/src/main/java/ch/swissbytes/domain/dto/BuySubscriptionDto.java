package ch.swissbytes.domain.dto;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BuySubscriptionDto implements Serializable {
    private Long servicePlanId;         // plan witch user paying.
    private String imei;                // imei device from paying.
    private Integer tigoPhoneNumber;    // phone number only bolivia.
    private String tigoIdCardNumber;    // CI.
    private Long nit;                   // NIT.
    private String businessName;        // name of the social reason.
    private Long deviceId;              // device id witch user paying.

    public static BuySubscriptionDto createNew() {
        BuySubscriptionDto subscriptionDto = new BuySubscriptionDto();
        subscriptionDto.servicePlanId = EntityUtil.DEFAULT_LONG;
        subscriptionDto.imei = EntityUtil.DEFAULT_STRING;
        subscriptionDto.tigoPhoneNumber = EntityUtil.DEFAULT_INTEGER;
        subscriptionDto.tigoIdCardNumber = EntityUtil.DEFAULT_STRING;
        subscriptionDto.nit = EntityUtil.DEFAULT_LONG;
        subscriptionDto.businessName = EntityUtil.DEFAULT_BUSINESS_NAME;
        return subscriptionDto;
    }

}
