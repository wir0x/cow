package ch.swissbytes.module.buho.service.notifications.device;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.notifications.mail.template.device.sos.MailSosAlert;
import ch.swissbytes.module.shared.notifications.push_notification.device.sos.PushNotificationSos;
import ch.swissbytes.module.shared.notifications.sms.template.device.sos.SmsSosAlert;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
public class SosAlertService {

    @Inject
    private Logger log;
    @Inject
    private MailSosAlert mailNotification;
    @Inject
    private SmsSosAlert smsNotification;
    @Inject
    private PushNotificationSos pushNotification;
    @Inject
    private DeviceService deviceService;


    public void notification(Alarm alarm) {
        Optional<Device> device = deviceService.getById(alarm.getDeviceId());

        if (device.isPresent())
            sendNotifications(device.get(), alarm);
    }

    private void sendNotifications(Device device, Alarm alarm) {
        log.info(String.format("Send notification... Device: %s Alarm: %s", device.getId(), alarm.getType()));
        alarm.setSosOldPosition(isSosOldPosition(alarm));
        pushNotification.add(device, alarm);
        smsNotification.add(device, alarm);
        mailNotification.add(device, alarm);
    }

    private boolean isSosOldPosition(Alarm alarm) {
        final long inTmSos = KeyAppConfiguration.getLong(ConfigurationKey.TIME_SOS_INTERVAL);
        final long diffMil = new Date().getTime() - alarm.getDateReceived().getTime();
        final long diffSec = diffMil / 1000;
        final long diffMin = diffSec / 60;
        return diffMin > inTmSos;
    }
}
