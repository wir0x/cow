package ch.swissbytes.module.buho.rest.v1.dto;

import ch.swissbytes.module.buho.app.position.model.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class PositionDto {

    private Long id;
    private double latitude;
    private double longitude;
    private double speed;
    private String battery;
    private Date date;
    private boolean isActual;


    public static PositionDto from(Position position) {
        PositionDto dto = new PositionDto();
        dto.id = position.getId();
        dto.latitude = position.getLatitude();
        dto.longitude = position.getLongitude();
        dto.speed = position.getSpeed();
        dto.battery = position.getBattery();
        dto.date = position.getTime();
        dto.isActual = isPositionActual(position);
        return dto;
    }

    private static boolean isPositionActual(Position position) {
        return (position.getTime().getTime() + 90000) >= (new Date().getTime());
    }
}
