package ch.swissbytes.module.buho.service.notifications.device;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.shared.notifications.mail.template.device.lowbattery.MailLowBatteryAlert;
import ch.swissbytes.module.shared.notifications.push_notification.device.lowbattery.PushNotificationLowBattery;
import ch.swissbytes.module.shared.notifications.sms.template.device.lowbattery.SmsLowBatteryAlert;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class LowBatteryAlertService {

    @Inject
    private Logger log;
    @Inject
    private DeviceService deviceService;
    @Inject
    private MailLowBatteryAlert mailNotification;
    @Inject
    private SmsLowBatteryAlert smsNotification;
    @Inject
    private PushNotificationLowBattery pushNotification;


    public void notification(Alarm alarm) {
        Optional<Device> device = deviceService.getById(alarm.getDeviceId());

        if (device.isPresent())
            sendNotifications(device.get(), alarm);
    }

    private void sendNotifications(Device device, Alarm alarm) {
        log.info(String.format("Send notification... Device: %s Alarm: %s", device.getId(), alarm.getType()));
        pushNotification.add(device, alarm);
        smsNotification.add(device, alarm);
        mailNotification.add(device, alarm);
    }
}
