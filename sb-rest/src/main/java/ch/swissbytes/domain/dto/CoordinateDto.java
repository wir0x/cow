package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CoordinateDto implements Serializable {

    private String latitude;
    private String longitude;

    public static CoordinateDto createNew() {
        CoordinateDto coordinateDto = new CoordinateDto();
        coordinateDto.latitude = EntityUtil.DEFAULT_STRING;
        coordinateDto.longitude = EntityUtil.DEFAULT_STRING;
        return coordinateDto;
    }

    public static CoordinateDto fromCoordinate(Coordinate coordinate) {
        CoordinateDto coordinateDto = createNew();
        coordinateDto.latitude = coordinate.getLatitude().toString();
        coordinateDto.longitude = coordinate.getLongitude().toString();
        return coordinateDto;
    }
}
