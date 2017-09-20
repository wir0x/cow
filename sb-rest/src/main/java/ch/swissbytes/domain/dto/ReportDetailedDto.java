package ch.swissbytes.domain.dto;

import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Getter
@Setter
public class ReportDetailedDto implements Serializable {

    private Integer stopNumbers;
    private Double parkTime;
    private Double travelTime;
    private Double travelDistance;
    private Double travelMaxSpeed;
    private Double travelAvgSpeed;
    private List<TravelDto> travels;


    public static ReportDetailedDto createNew() {
        ReportDetailedDto reportDetailedDto = new ReportDetailedDto();
        reportDetailedDto.stopNumbers = EntityUtil.DEFAULT_INTEGER;
        reportDetailedDto.parkTime = EntityUtil.DEFAULT_DOUBLE;
        reportDetailedDto.travelTime = EntityUtil.DEFAULT_DOUBLE;
        reportDetailedDto.travelDistance = EntityUtil.DEFAULT_DOUBLE;
        reportDetailedDto.travelMaxSpeed = EntityUtil.DEFAULT_DOUBLE;
        reportDetailedDto.travels = Collections.emptyList();
        return reportDetailedDto;
    }

    public static ReportDetailedDto fromPositionList(List<TravelDto> travels) {
        ReportDetailedDto reportDetailedDto = new ReportDetailedDto();
        reportDetailedDto.stopNumbers = travels.size();
        reportDetailedDto.parkTime = travels.stream().mapToDouble(TravelDto::getTimeParked).sum() / 60;
        reportDetailedDto.travelTime = travels.stream().mapToDouble(TravelDto::getTimeTraveled).sum() / 60;
        reportDetailedDto.travelDistance = LongUtil.decimalFormat(travels.stream().mapToDouble(TravelDto::getDistance).sum(), 2);
        reportDetailedDto.travelMaxSpeed = travels.stream().max(Comparator.comparing(TravelDto::getMaxSpeed)).get().getMaxSpeed();
        reportDetailedDto.travelAvgSpeed = travels.stream().max(Comparator.comparing(TravelDto::getAvgSpeed)).get().getAvgSpeed();
        reportDetailedDto.travels = travels;
        return reportDetailedDto;
    }
}
