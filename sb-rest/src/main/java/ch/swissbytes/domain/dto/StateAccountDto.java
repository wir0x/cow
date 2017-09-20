package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
public class StateAccountDto implements Serializable {

    private String deviceName;
    private String endSubscription;
    private List<SubscriptionDto> subscriptionDtoList;

    public static StateAccountDto createNew() {
        StateAccountDto stateAccountDto = new StateAccountDto();
        stateAccountDto.deviceName = EntityUtil.DEFAULT_STRING;
        stateAccountDto.endSubscription = EntityUtil.DEFAULT_STRING;
        stateAccountDto.subscriptionDtoList = null;
        return stateAccountDto;
    }

    public static StateAccountDto fromEntity(Subscription subscription) {
        StateAccountDto stateAccountDto = StateAccountDto.createNew();
        stateAccountDto.deviceName = subscription.getDevice().getName();
        stateAccountDto.endSubscription = DateUtil.getSimpleDate(subscription.getEndDate());
        stateAccountDto.subscriptionDtoList = null;
        return stateAccountDto;
    }
}
