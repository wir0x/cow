package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.position.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PositionReportDto implements Serializable {

    private Long id;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Date date;

    public static PositionReportDto fromPosition(Position position, Long id) {
        PositionReportDto positionReportDto = new PositionReportDto();
        positionReportDto.id = id;
        positionReportDto.latitude = position.getLatitude();
        positionReportDto.longitude = position.getLongitude();
        positionReportDto.speed = position.getSpeed();
        positionReportDto.date = position.getTime();
        return positionReportDto;
    }

    public static List<PositionReportDto> fromPositionList(List<Position> positionList) {
        List<PositionReportDto> positionReportDtoList = new ArrayList<>();
        Long i = 1L;

        for (Position position : positionList) {
            positionReportDtoList.add(fromPosition(position, i++));
        }
        return positionReportDtoList;
    }
}
