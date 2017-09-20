package ch.swissbytes.module.buho.service.notifications.device;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.shared.notifications.mail.template.device.dropwatch.MailDropWatchSender;
import ch.swissbytes.module.shared.notifications.push_notification.device.dropwatch.PushNotificationDropWatch;
import ch.swissbytes.module.shared.notifications.sms.template.device.dropwatch.SmsDropWatchSender;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DropWatchAlertService {

    @Inject
    private Logger log;
    @Inject
    private MailDropWatchSender mailNotification;
    @Inject
    private SmsDropWatchSender smsNotification;
    @Inject
    private PushNotificationDropWatch pushNotification;
    @Inject
    private DeviceService deviceService;


    public void notification(Alarm alarm) {
        Optional<Device> device = deviceService.getById(alarm.getDeviceId());

        if (device.isPresent())
            sendNotifications(device.get(), alarm);
    }

    private void sendNotifications(Device device, Alarm alarm) {
        log.info(String.format("Send notification... Device: %s Alarm: %s", device.getId(), alarm.getType()));
        smsNotification.add(device, alarm);
        pushNotification.add(device, alarm);
        mailNotification.add(device, alarm);
    }
}
