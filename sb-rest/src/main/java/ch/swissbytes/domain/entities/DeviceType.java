package ch.swissbytes.domain.entities;

import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "device_type")
@Getter
@Setter
public class DeviceType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    @Size(min = 1, message = "La descripcion del dispositivo es requerido")
    private String description;

    @Column(name = "alarm_sos")
    private Boolean alarmSOS;

    @Column(name = "alarm_battery")
    private Boolean alarmBattery;

    @Column(name = "battery")
    private Boolean battery;

    @Column(name = "report_limit")
    private Integer reportLimit;

    @Column(name = "drop_watch_alert")
    private Boolean dropWatchAlarm;

    public DeviceType(Long id) {
        this.id = id;
    }

    public DeviceType() {

    }

    @Transient
    public static DeviceType createNew() {
        DeviceType deviceType = new DeviceType();
        deviceType.id = EntityUtil.DEFAULT_LONG;
        deviceType.description = EntityUtil.DEFAULT_STRING;
        deviceType.alarmSOS = EntityUtil.DEFAULT_BOOLEAN;
        deviceType.alarmBattery = EntityUtil.DEFAULT_BOOLEAN;
        deviceType.battery = EntityUtil.DEFAULT_BOOLEAN;
        return deviceType;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof DeviceType)) return false;

        DeviceType other = (DeviceType) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.intValue();
    }
}
