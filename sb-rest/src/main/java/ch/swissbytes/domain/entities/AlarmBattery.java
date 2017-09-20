package ch.swissbytes.domain.entities;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "alarm_battery")
@Getter
@Setter
@ToString
public class AlarmBattery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "level_battery")
    private Long levelBattery;

    @Column(name = "register_date")
    private Date registerDate;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_alarm_battery_device_id"))
    private Device device;

    public static AlarmBattery createNew() {
        AlarmBattery alarmBattery = new AlarmBattery();
        alarmBattery.id = EntityUtil.DEFAULT_LONG;
        alarmBattery.levelBattery = EntityUtil.DEFAULT_LONG;
        alarmBattery.registerDate = EntityUtil.DEFAULT_DATE;
        alarmBattery.device = Device.createNew();
        return alarmBattery;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AlarmBattery)) return false;

        AlarmBattery other = (AlarmBattery) obj;
        return this.id == other.id;
    }
}
