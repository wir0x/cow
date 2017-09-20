package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.PositionUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class TravelDto implements Serializable {

    private Date startParked;
    private Date endParked;
    private Date startTraveled;
    private Date endTraveled;
    private Double distance;
    private Double maxSpeed;
    private Double avgSpeed;
    private Double timeParked;
    private Double timeTraveled;
    private List<PositionReportDto> positions;


    public static TravelDto createNew() {
        TravelDto travelDto = new TravelDto();
        travelDto.startParked = EntityUtil.DEFAULT_DATE;
        travelDto.endParked = EntityUtil.DEFAULT_DATE;
        travelDto.startTraveled = EntityUtil.DEFAULT_DATE;
        travelDto.endTraveled = EntityUtil.DEFAULT_DATE;
        travelDto.avgSpeed = EntityUtil.DEFAULT_DOUBLE;
        travelDto.timeParked = EntityUtil.DEFAULT_DOUBLE;
        travelDto.distance = EntityUtil.DEFAULT_DOUBLE;
        travelDto.maxSpeed = EntityUtil.DEFAULT_DOUBLE;
        travelDto.positions = new ArrayList<>();
        return travelDto;
    }

    public static TravelDto from(List<Position> positionList) {
        TravelDto travelDto = createNew();
        travelDto.startParked = PositionUtil.startParked(positionList);
        travelDto.endParked = PositionUtil.endParked(positionList);
        travelDto.startTraveled = PositionUtil.startTraveled(positionList);
        travelDto.endTraveled = PositionUtil.endTraveled(positionList);
        travelDto.timeParked = PositionUtil.timeParked(positionList);
        travelDto.timeTraveled = PositionUtil.timeTraveled(positionList);
        travelDto.distance = PositionUtil.distance(positionList);
        travelDto.maxSpeed = PositionUtil.maxSpeed(positionList);
        travelDto.avgSpeed = PositionUtil.avgSpeed(positionList);
        travelDto.positions = PositionReportDto.fromPositionList(positionList);
        return travelDto;
    }


}
