package ch.swissbytes.module.shared.notifications.sms.template.device.dropwatch;

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
public class SmsDropWatchSender implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public void add(Device device, Alarm alarm) {
        final String sms = device.getCellphoneDropAlarm();
        final Account account = device.getAccount();

        if (StringUtil.isNotEmpty(sms) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm);
    }

    public void makeNotification(Device device, Alarm alarm) {
        final String[] cellphones = device.getCellphoneDropAlarm().split("(;)|(\\,)|(\\r?\\n)");

        for (String cellphone : cellphones) {
            try {
                String content = getContent(device, alarm);
                String subject = labels.getMessage("alarm.drop.watch");
                Notification notification = Notification.newForSmsNotification(device, alarm, cellphone, subject, content);
                notificationService.add(notification);
                log.info(String.format("SMS Notification Created - Account: %s Device: %s Cellphone: %s ", device.getAccount().getName(), device.getId(), cellphone));
            } catch (InvalidInputException e) {
                log.warn(e);
            }
        }
    }

    public String getContent(Device device, Alarm alarm) {
        StringBuilder bodyMessage = new StringBuilder();
        bodyMessage.append(labels.getMessage("attention"));
        bodyMessage.append(" ");
        bodyMessage.append(labels.getMessage("the.watch.of"));
        bodyMessage.append(": ");
        bodyMessage.append(device.getName());
        bodyMessage.append(" ");
        bodyMessage.append(labels.getMessage("was.removed.from.hand"));
        bodyMessage.append(" ");
        bodyMessage.append(labels.getMessage("in"));
        bodyMessage.append("\nhttps://www.google.com/maps?q=" + alarm.getLatitude() + "," + alarm.getLongitude());
        return bodyMessage.toString();
    }
}
