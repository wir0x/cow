package ch.swissbytes.module.shared.notifications.sms.template.device.batterysupply;

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
import java.util.Objects;

@Stateless
public class SmsSupplyBatteryAlert implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public void addConnected(Device device, Alarm alarm) {
        final String sms = device.getBatteryCellphones();
        final Account account = device.getAccount();
        final String subject = labels.getMessage("alarm.external.battery.connected");

        if (StringUtil.isNotEmpty(sms) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm, subject);
    }

    public void addDisconnected(Device device, Alarm alarm) {
        final String sms = device.getBatteryCellphones();
        final Account account = device.getAccount();
        final String subject = labels.getMessage("alarm.external.battery.disconnected");

        if (StringUtil.isNotEmpty(sms) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm, subject);
    }

    private void makeNotification(Device device, Alarm alarm, String subject) {
        final String[] cellphones = device.getBatteryCellphones().split("(;)|(\\,)|(\\r?\\n)");

        for (String cellphone : cellphones) {
            try {
                final String batterySupply = batterySupplyBySubject(subject);
                final String content = getContent(device, alarm, batterySupply);
                final Notification notification = Notification.newForSmsNotification(device, alarm, cellphone, subject, content);
                notificationService.add(notification);
            } catch (InvalidInputException e) {
                log.warn(e);
            }
        }
    }

    private String batterySupplyBySubject(String subject) {
        final String connected = labels.getMessage("battery.msg.battery.supply.connected");
        final String disconnected = labels.getMessage("battery.msg.battery.supply.disconnected");
        return Objects.equals(subject, connected) ? connected : disconnected;
    }

    public String getContent(Device device, Alarm alarm, String batterySupply) {
        StringBuilder bodyMessage = new StringBuilder();
        bodyMessage.append(labels.getMessage("fence.msg.device"));
        bodyMessage.append(" ");
        bodyMessage.append(device.getName());
        bodyMessage.append(" ");
        bodyMessage.append(batterySupply);
        bodyMessage.append("\nhttps://www.google.com/maps?q=" + alarm.getLatitude() + "," + alarm.getLongitude());
        return bodyMessage.toString();
    }


}
