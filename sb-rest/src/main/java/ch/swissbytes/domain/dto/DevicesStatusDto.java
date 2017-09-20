package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class DevicesStatusDto implements Serializable {

    private AccountDto account;
    private DeviceDto device;
    private SubscriptionDto subscription;
    private PositionDto position;

    public static DevicesStatusDto from(Device device, Subscription subscription, Position position) {
        DevicesStatusDto dto = new DevicesStatusDto();
        dto.device = DeviceDto.from(device);
        dto.account = device.getAccount() != null ? AccountDto.fromAccountEntity(device.getAccount()) : AccountDto.createNew();
        dto.subscription = subscription.getId() != null ? SubscriptionDto.fromSubscriptionEntity(subscription) : SubscriptionDto.createNew();
        dto.position = position.getId() != null ? PositionDto.fromPosition(position, device, 1) : PositionDto.createNew();
        return dto;
    }

}
