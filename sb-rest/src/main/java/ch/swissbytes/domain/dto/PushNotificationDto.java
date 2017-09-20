package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PushNotificationDto implements Serializable {
    private String token;
    private String title;
    private String message;
    private PushNotificationType notificationType;
    private Object innerData;

    public static PushNotificationDto createNew() {
        PushNotificationDto dto = new PushNotificationDto();
        dto.token = EntityUtil.DEFAULT_STRING;
        dto.title = EntityUtil.DEFAULT_STRING;
        dto.message = EntityUtil.DEFAULT_STRING;
        dto.notificationType = null;
        dto.innerData = null;
        return dto;
    }
}