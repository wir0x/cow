package ch.swissbytes.module.shared.notifications.mail.template.device.lowbattery;

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
import java.util.Calendar;

public class MailLowBatteryAlert implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public String getResource() {
        String result = "";

        try {
            InputStream in = EmailSenderPasswordRecovery.class.getResourceAsStream("/email-template/lowBatteryNotification.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
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
        return (string.indexOf(emailKey) > 0) ? string.replace("{" + emailKey + "}", msgKey) : string;
    }

    public String getYear() {
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void add(Device device, Alarm alarm) {
        final String mails = device.getBatteryMails();
        final Account account = device.getAccount();

        if (StringUtil.isNotEmpty(mails) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm);
    }

    private void makeNotification(Device device, Alarm alarm) {
        final String address = device.getBatteryMails();
        final String subject = labels.getMessage("alarm.low.battery");
        final String content = getContent(device, alarm);
        final Notification notification = Notification.newForMailNotification(device, alarm, address, subject, content);
        notificationService.add(notification);
    }

    private String getContent(Device device, Alarm alarm) {
        String content = getResource();
        content = rpl(content, "message.device", labels.getMessage("fence.msg.device"));
        content = rpl(content, "deviceName", device.getName());
        content = rpl(content, "msg.msg.battery", labels.getMessage("msg.msg.battery"));
        content = rpl(content, "msg.low.battery.level", "(" + alarm.getBattery() + "%)");
        content = rpl(content, "msg.at.time", labels.getMessage("msg.at.time"));
        content = rpl(content, "device.last.position", DateUtil.SDF_DETAIL.format(alarm.getDateReceived()));
        content = rpl(content, "google.maps.url", "\n http:maps.google.com/maps?q=" + alarm.getLatitude() + "%2c" + alarm.getLongitude());
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
