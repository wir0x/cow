package ch.swissbytes.module.shared.notifications.mail.template.device.batterysupply;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.notifications.mail.template.passwordrecovery.EmailSenderPasswordRecovery;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;

public class MailSupplyBatteryAlert implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public String getResource() {
        String result = "";

        try {
            InputStream in = EmailSenderPasswordRecovery.class.getResourceAsStream("/email-template/batteryNotification.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    protected String rpl(String string, String emailKey, String msgKey) {
        if (string.indexOf(emailKey) > 0) {
            return string.replace("{" + emailKey + "}", msgKey);
        } else {
            return string;
        }
    }

    public String getYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void addConnected(Device device, Alarm alarm) {
        final String mails = device.getBatteryMails();
        final Account account = device.getAccount();
        final String subject = labels.getMessage("alarm.external.battery.connected");

        if (StringUtil.isNotEmpty(mails) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm, subject);
    }

    public void addDisconnected(Device device, Alarm alarm) {
        final String mails = device.getBatteryMails();
        final Account account = device.getAccount();
        final String subject = labels.getMessage("alarm.external.battery.disconnected");

        if (StringUtil.isNotEmpty(mails) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm, subject);
    }

    private void makeNotification(Device device, Alarm alarm, String subject) {
        final String address = device.getBatteryMails();
        final String supplyBattery = supplyBatteryBySubject(subject);
        final String content = contentBatteryConnected(device, alarm, supplyBattery);
        final Notification notification = Notification.newForMailNotification(device, alarm, address, subject, content);
        notificationService.add(notification);
    }

    private String supplyBatteryBySubject(String subject) {
        final String connected = labels.getMessage("alarm.external.battery.connected");
        final String disconnected = labels.getMessage("alarm.external.battery.disconnected");
        return Objects.equals(subject, connected) ? connected : disconnected;
    }

    private String contentBatteryConnected(Device device, Alarm alarm, String supplyBattery) {
        String content = getResource();
        content = rpl(content, "deviceName", device.getName());
        content = rpl(content, "fence.message.device", labels.getMessage("fence.msg.device"));
        content = rpl(content, "battery.msg.battery.supply", supplyBattery);
        content = rpl(content, "fence.msg.at.time", labels.getMessage("msg.at.time"));
        content = rpl(content, "device.last.position", DateUtil.SDF_DETAIL.format(alarm.getDateReceived()));
        content = rpl(content, "google.maps.url", "\n http:maps.google.com/maps?q=" + alarm.getLatitude() + "%2c" + alarm.getLongitude());
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
