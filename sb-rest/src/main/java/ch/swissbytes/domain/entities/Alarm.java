package ch.swissbytes.domain.entities;

import ch.swissbytes.module.buho.app.position.model.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@ToString
public class Alarm {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "date_received")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReceived;

    @Column(name = "date_sent")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSent;

    @Column(name = "is_sent")
    private boolean isSend;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "battery")
    private Integer battery;

    @Transient
    private boolean isSosOldPosition;

    public static Alarm createSOSFromPosition(Position position) {
        Alarm alarm = new Alarm();
        alarm.type = "GTSOS";
        alarm.deviceId = position.getDevice().getId();
        alarm.isSend = false;
        alarm.dateReceived = position.getSentDate();
        alarm.dateSent = null;
        alarm.latitude = position.getLatitude();
        alarm.longitude = position.getLongitude();
        alarm.speed = position.getSpeed();
        final Double battery = position.getBattery() == null || position.getBattery().isEmpty() ? 0.0 : Double.parseDouble(position.getBattery());
        alarm.battery = battery.intValue();
        return alarm;
    }
}
