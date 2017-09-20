package ch.swissbytes.module.shared.notifications.mail.template.device.dropwatch;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
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

public class MailDropWatchSender implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;


    public String getResource() {
        String result = "";

        try {
            InputStream in = MailDropWatchSender.class.getResourceAsStream("/email-template/dropWatchMailTemplate.html");
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
        return (string.indexOf(emailKey) > 0) ? string.replace("{" + emailKey + "}", msgKey) : string;
    }

    public String getYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void add(Device device, Alarm alarm) {
        final String mails = device.getMailsDropAlarm();
        final Account account = device.getAccount();

        if (StringUtil.isNotEmpty(mails) && EntityUtil.isNotNull(account))
            makeNotification(device, alarm);
    }

    private void makeNotification(Device device, Alarm alarm) {
        final String address = device.getMailsDropAlarm();
        final String subject = labels.getMessage("alarm.drop.watch");
        final String content = getContent(device, alarm);
        final Notification notification = Notification.newForMailNotification(device, alarm, address, subject, content);
        notificationService.add(notification);
    }

    private String getContent(Device device, Alarm alarm) {
        String content = getResource();
        content = rpl(content, "alert.buho.kids", labels.getMessage("alert.buho.kids"));
        content = rpl(content, "attention", labels.getMessage("attention"));
        content = rpl(content, "the.watch.of", labels.getMessage("the.watch.of"));
        content = rpl(content, "device.name", device.getName());
        content = rpl(content, "was.removed.from.hand", labels.getMessage("was.removed.from.hand"));
        content = rpl(content, "to.hours", labels.getMessage("to.hours"));
        content = rpl(content, "time", DateUtil.getSimpleDateTime(alarm.getDateReceived()));
        content = rpl(content, "in", labels.getMessage("in"));
        content = rpl(content, "google.maps.url", "\n http:maps.google.com/maps?q=" + alarm.getLatitude() + "%2c" + alarm.getLongitude());
        content = rpl(content, "current.date", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
