package ch.swissbytes.module.buho.rest.v1.dto;

import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SubscriptionDto {

    private Long id;
    private Date start;
    private Date end;
    private String status;

    public static SubscriptionDto from(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.id = subscription.getId();
        dto.start = subscription.getStartDate();
        dto.end = subscription.getEndDate();
        dto.status = subscription.getStatus().getLabel();
        return dto;
    }
}
