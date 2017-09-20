package ch.swissbytes.module.buho.rest.v1.dto;


import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.persistence.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentPositionDto {

    private DeviceDto device;
    private PositionDto position;
    private SubscriptionDto subscription;

    public static CurrentPositionDto fromByDeviceAndPosition(Device device, Optional<Position> position) {
        CurrentPositionDto dto = new CurrentPositionDto();
        dto.device = DeviceDto.from(device);
        dto.position = position.isPresent() ? PositionDto.from(position.get()) : null;
        return dto;
    }

    public static CurrentPositionDto from(Device device, Position position, Subscription subscription) {
        CurrentPositionDto dto = new CurrentPositionDto();
        dto.device = DeviceDto.from(device);
        dto.position = PositionDto.from(position);
        dto.subscription = SubscriptionDto.from(subscription);
        return dto;
    }

    public static CurrentPositionDto withoutPosition(Device device, Subscription subscription) {
        CurrentPositionDto dto = new CurrentPositionDto();
        dto.device = DeviceDto.from(device);
        dto.position = null;
        dto.subscription = SubscriptionDto.from(subscription);
        return dto;
    }
}
