package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PaymentPushNotificationDto extends PushNotificationDto {
    private Integer errorType;

    private static PaymentPushNotificationDto parsePushNotificationDto(PushNotificationDto pushDto, Integer errorType) {
        PaymentPushNotificationDto paymentPushDto = new PaymentPushNotificationDto();
        paymentPushDto.setErrorType(errorType);
        paymentPushDto.setToken(pushDto.getToken());
        paymentPushDto.setInnerData(pushDto.getInnerData());
        paymentPushDto.setToken(pushDto.getToken());
        paymentPushDto.setNotificationType(PushNotificationType.PAYMENT_RESPONSE);
        paymentPushDto.setMessage(pushDto.getMessage());
        paymentPushDto.setTitle(pushDto.getTitle());
        return paymentPushDto;
    }

    public static PaymentPushNotificationDto createNew() {
        return parsePushNotificationDto(PushNotificationDto.createNew(), EntityUtil.DEFAULT_INTEGER);
    }
}
