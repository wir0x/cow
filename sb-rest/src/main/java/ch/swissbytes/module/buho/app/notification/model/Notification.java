package ch.swissbytes.module.buho.app.notification.model;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@ToString
public class Notification {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_name")
    private String fromName;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "sent_date")
    private Date sentDate;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "device_id")
    private Long deviceId;


    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    public static Notification createNew() {
        Notification notification = new Notification();
        notification.fromName = EntityUtil.DEFAULT_STRING;
        notification.fromAddress = EntityUtil.DEFAULT_STRING;
        notification.toAddress = EntityUtil.DEFAULT_STRING;
        notification.subject = EntityUtil.DEFAULT_STRING;
        notification.content = EntityUtil.DEFAULT_STRING;
        notification.status = PaymentStatusEnum.PENDING;
        notification.createdDate = EntityUtil.DEFAULT_DATE;
        notification.sentDate = EntityUtil.DEFAULT_DATE;
        notification.errorDescription = EntityUtil.DEFAULT_STRING;
        return notification;
    }

    private static Notification frameNotification(Device device, Alarm alarm, String address, String subject, String content) {
        Notification notification = Notification.createNew();
        notification.subject = subject;
        notification.toAddress = address;
        notification.content = content;
        notification.accountId = device.getAccount() != null ? device.getAccount().getId() : 0L;
        notification.deviceId = device.getId();
        notification.createdDate = alarm.getDateReceived();
        return notification;
    }

    public static Notification newForSmsNotification(Device device, Alarm alarm, String address, String subject, String content) {
        Notification notification = frameNotification(device, alarm, address, subject, content);
        notification.type = NotificationTypeEnum.SMS;
        return notification;
    }

    public static Notification newForPushNotification(Device device, Alarm alarm, String cellphone, String subject, String content) {
        Notification notification = frameNotification(device, alarm, cellphone, subject, content);
        notification.type = NotificationTypeEnum.PUSH_NOTIFICATION;
        return notification;
    }

    public static Notification newForMailNotification(Device device, Alarm alarm, String cellphone, String subject, String content) {
        Notification notification = frameNotification(device, alarm, cellphone, subject, content);
        notification.type = NotificationTypeEnum.EMAIL;
        return notification;
    }
}
