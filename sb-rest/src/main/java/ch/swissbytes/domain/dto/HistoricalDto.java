package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.PositionUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class HistoricalDto implements Serializable {

    private Date firstPosition;
    private Date lastPosition;
    private Double maxSpeed;
    private Double avgSpeed;
    private Double distanceTraveled;
    private DeviceDto device;
    private SubscriptionDto subscription;
    private List<PositionReportDto> positions;


    public static HistoricalDto createNew() {
        HistoricalDto dto = new HistoricalDto();
        dto.firstPosition = EntityUtil.DEFAULT_DATE;
        dto.lastPosition = EntityUtil.DEFAULT_DATE;
        dto.maxSpeed = EntityUtil.DEFAULT_DOUBLE;
        dto.avgSpeed = EntityUtil.DEFAULT_DOUBLE;
        dto.distanceTraveled = EntityUtil.DEFAULT_DOUBLE;
        dto.device = DeviceDto.createNew();
        dto.subscription = SubscriptionDto.createNew();
        dto.positions = null;
        return dto;
    }

    public static HistoricalDto withoutSubscription(Device device) {
        HistoricalDto dto = createNew();
        dto.setDevice(DeviceDto.from(device));
        dto.setSubscription(SubscriptionDto.newWithoutSubscription());
        return dto;
    }

    public static HistoricalDto withSubscription(Subscription subscription) {
        HistoricalDto dto = createNew();
        dto.device = DeviceDto.from(subscription.getDevice());
        dto.subscription = SubscriptionDto.fromSubscriptionEntity(subscription);
        return dto;
    }

    public static HistoricalDto from(List<Position> positionList, Device device, Subscription subscription) {
        HistoricalDto dto = createNew();
        dto.firstPosition = PositionUtil.startTraveled(positionList);
        dto.lastPosition = PositionUtil.endTraveled(positionList);
        dto.maxSpeed = PositionUtil.maxSpeed(positionList);
        dto.avgSpeed = PositionUtil.avgSpeed(positionList);
        dto.distanceTraveled = PositionUtil.distance(positionList);
        dto.device = DeviceDto.from(device);
        dto.positions = PositionReportDto.fromPositionList(positionList);
        dto.subscription = SubscriptionDto.fromSubscriptionEntity(subscription);
        return dto;
    }
}
