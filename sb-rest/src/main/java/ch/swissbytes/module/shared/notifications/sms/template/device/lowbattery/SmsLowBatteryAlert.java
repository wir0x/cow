package ch.swissbytes.module.shared.notifications.sms.template.device.lowbattery;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;

@Stateless
public class SmsLowBatteryAlert implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private AppConfiguration labels;
    @Inject
    private NotificationService notificationService;


    public void add(Device device, Alarm alarm) {
        final String sms = device.getBatteryCellphones();
        final Account account = device.getAccount();

        if (StringUtil.isNotEmpty(sms) && EntityUtil.isNotNull(account)) {
            makeNotification(device, alarm);
        }
    }

    private void makeNotification(Device device, Alarm alarm) {
        final String[] cellphones = device.getBatteryCellphones().split("(;)|(\\,)|(\\r?\\n)");

        for (String cellphone : cellphones) {
            try {
                final String content = getContent(device, alarm);
                final String subject = labels.getMessage("alarm.low.battery");
                final Notification notification = Notification.newForSmsNotification(device, alarm, cellphone, subject, content);
                notificationService.add(notification);
            } catch (InvalidInputException e) {
                log.warn(e);
            }
        }
    }

    public String getContent(Device device, Alarm alarm) {
        StringBuilder bodyMessage = new StringBuilder();
        bodyMessage.append(labels.getMessage("msg.low.battery"));
        bodyMessage.append(" ");
        bodyMessage.append("(" + alarm.getBattery() + "%)");
        bodyMessage.append(" ");
        bodyMessage.append(labels.getMessage("msg.gps"));
        bodyMessage.append(" ");
        bodyMessage.append(device.getName());
        bodyMessage.append(" ");
        bodyMessage.append("\nhttps://www.google.com/maps?q=" + alarm.getLatitude() + "," + alarm.getLongitude());
        bodyMessage.append("\n[BUHO]");
        return bodyMessage.toString();
    }
}
