package ch.swissbytes.module.buho.app.geofence.model;

import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "fence")
@Getter
@Setter
public class GeoFence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Size(min = 1, message = "El nombre de la cerca es requerido")
    private String name;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIME)
    private Date endTime;

    @Column(name = "is_entire_day")
    private Boolean entireDay;

    @Column(name = "sunday")
    private Boolean sunday;

    @Column(name = "monday")
    private Boolean monday;

    @Column(name = "tuesday")
    private Boolean tuesday;

    @Column(name = "wednesday")
    private Boolean wednesday;

    @Column(name = "thursday")
    private Boolean thursday;

    @Column(name = "friday")
    private Boolean friday;

    @Column(name = "saturday")
    private Boolean saturday;

    @Column(name = "is_entering_zone")
    private Boolean enteringZone;

    @Column(name = "is_leaving_zone")
    private Boolean leavingZone;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_fence_device_id"))
    private Device device;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "emails")
    private String emails;

    @Column(name = "cellphones")
    private String cellphones;

    @Column(name = "position_pointer")
    private Long positionPointer;

    @Column(name = "is_inside")
    private Boolean isInside;

    @Transient
    private List<Coordinate> coordinateList;

    public GeoFence(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GeoFence{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", entireDay=" + entireDay +
                ", sunday=" + sunday +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", enteringZone=" + enteringZone +
                ", leavingZone=" + leavingZone +
                ", device=" + device +
                ", status=" + status +
                ", emails='" + emails + '\'' +
                ", cellphones='" + cellphones + '\'' +
                ", positionPointer=" + positionPointer +
                ", isInside=" + isInside +
                '}';
    }

    public GeoFence() {
    }

    @Transient
    public static GeoFence createNew() {
        GeoFence geoFence = new GeoFence();
        geoFence.name = EntityUtil.DEFAULT_STRING;
        geoFence.status = StatusEnum.DISABLED;
        geoFence.startTime = EntityUtil.DEFAULT_DATE;
        geoFence.endTime = EntityUtil.DEFAULT_DATE;
        geoFence.entireDay = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.sunday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.monday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.tuesday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.wednesday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.thursday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.friday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.saturday = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.enteringZone = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.leavingZone = EntityUtil.DEFAULT_BOOLEAN;
        geoFence.device = null;
        return geoFence;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }
}
