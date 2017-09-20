package ch.swissbytes.domain.entities;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sms_control", uniqueConstraints = {@UniqueConstraint(name = "idx_sms_control_unique", columnNames = {"device_id", "month_year"})})
@Getter
@Setter
public class SmsControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_sms_control_device_id"))
    @JsonIgnore
    private Device device;

    @Column(name = "month_year")
    @Temporal(TemporalType.DATE)
    private Date monthYear;

    @Column(name = "max_sms")
    private Integer maxSms;

    @Column(name = "used_sms")
    private Integer usedSms;

    @Column(name = "sent_mail")
    private Boolean sentMail;

    @Transient
    public static SmsControl createNew() {
        SmsControl smsControl = new SmsControl();
        smsControl.monthYear = EntityUtil.DEFAULT_DATE;
        smsControl.maxSms = EntityUtil.DEFAULT_INTEGER;
        smsControl.usedSms = EntityUtil.DEFAULT_INTEGER;
        smsControl.sentMail = EntityUtil.DEFAULT_BOOLEAN;
        smsControl.device = null;
        return smsControl;
    }

    @Transient
    @JsonIgnore
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    @Transient
    public Long getDeviceId() {
        if (device == null)
            return null;
        return device.getId();
    }

    @Transient
    public void setDeviceId(Long deviceId) {
        if (LongUtil.isEmpty(deviceId)) {
            device = null;
        }
        device = new Device(deviceId);
    }
}
