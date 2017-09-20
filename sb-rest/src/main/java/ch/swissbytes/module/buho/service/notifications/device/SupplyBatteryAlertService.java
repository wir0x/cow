package ch.swissbytes.module.buho.service.notifications.device;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.shared.notifications.mail.template.device.batterysupply.MailSupplyBatteryAlert;
import ch.swissbytes.module.shared.notifications.push_notification.device.batterysupply.PushNotificationSupplyBattery;
import ch.swissbytes.module.shared.notifications.sms.template.device.batterysupply.SmsSupplyBatteryAlert;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class SupplyBatteryAlertService {

    @Inject
    private Logger log;
    @Inject
    private MailSupplyBatteryAlert mailNotification;
    @Inject
    private SmsSupplyBatteryAlert smsNotification;
    @Inject
    private PushNotificationSupplyBattery pushNotification;
    @Inject
    private DeviceService deviceService;


    public void notificationConnected(Alarm alarm) {
        Optional<Device> device = deviceService.getById(alarm.getDeviceId());

        if (device.isPresent())
            connNotification(device.get(), alarm);
    }

    public void notificationDisconnected(Alarm alarm) {
        final Optional<Device> device = deviceService.getById(alarm.getDeviceId());

        if (device.isPresent())
            discNotification(device.get(), alarm);
    }

    private void connNotification(Device device, Alarm alarm) {
        log.info(String.format("Send notification... Device: %s Alarm: %s", device.getId(), alarm.getType()));
        pushNotification.addConnected(device, alarm);
        smsNotification.addConnected(device, alarm);
        mailNotification.addConnected(device, alarm);
    }

    private void discNotification(Device device, Alarm alarm) {
        log.info(String.format("Send notification... Device: %s Alarm: %s", device.getId(), alarm.getType()));
        pushNotification.addDisconnected(device, alarm);
        smsNotification.addDisconnected(device, alarm);
        mailNotification.addDisconnected(device, alarm);
    }
}
