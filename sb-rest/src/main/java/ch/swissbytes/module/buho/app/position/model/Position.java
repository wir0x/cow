package ch.swissbytes.module.buho.app.position.model;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "positions")
@Getter
@Setter
@ToString
public class Position {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Column(name = "valid")
    private Boolean valid;

    @Column(name = "altitude")
    private Double altitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "course")
    private Double course;

    @Column(name = "extended_info", columnDefinition = "TEXT")
    private String extendedInfo;

    @Column(name = "command")
    private String command;

    @Column(name = "battery")
    private String battery;

    @Column(name = "sent_date")
    private Date sentDate;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_position_device_id"))
    private Device device;

    @Transient
    private Boolean actual;

    @Transient
    private Double maxSpeed;

    @Transient
    private Double avgSpeed;

    @Transient
    private int idleTime;

    @Transient
    private Double distance;

    public Position(Long id) {
        this.id = id;
    }

    public Position() {
    }

    public static Position createNew() {
        Position position = new Position();
        position.id = EntityUtil.DEFAULT_LONG;
        position.time = EntityUtil.DEFAULT_DATE;
        position.valid = EntityUtil.DEFAULT_BOOLEAN;
        position.altitude = EntityUtil.DEFAULT_DOUBLE;
        position.longitude = EntityUtil.DEFAULT_DOUBLE;
        position.longitude = EntityUtil.DEFAULT_DOUBLE;
        position.speed = EntityUtil.DEFAULT_DOUBLE;
        position.course = EntityUtil.DEFAULT_DOUBLE;
        position.extendedInfo = EntityUtil.DEFAULT_STRING;
        position.command = EntityUtil.DEFAULT_STRING;
        position.battery = EntityUtil.DEFAULT_STRING;
        position.actual = EntityUtil.DEFAULT_BOOLEAN;
        position.maxSpeed = EntityUtil.DEFAULT_DOUBLE;
        position.avgSpeed = EntityUtil.DEFAULT_DOUBLE;
        position.idleTime = EntityUtil.DEFAULT_INTEGER;
        position.distance = EntityUtil.DEFAULT_DOUBLE;
        position.device = null;
        return position;
    }

    public Position clone() {
        Position position = new Position();
        position.id = id;
        position.time = time;
        position.valid = valid;
        position.altitude = altitude;
        position.longitude = longitude;
        position.longitude = longitude;
        position.speed = speed;
        position.course = course;
        position.extendedInfo = extendedInfo;
        position.command = command;
        position.battery = battery;
        position.actual = actual;
        position.maxSpeed = maxSpeed;
        position.avgSpeed = avgSpeed;
        position.idleTime = idleTime;
        position.distance = distance;
        position.device = device;
        return position;
    }
}
