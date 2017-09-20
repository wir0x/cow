package ch.swissbytes.module.shared.notifications.mail.template.subscription;

import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class EmailSenderSubscriptionInvitation implements Serializable {

    @Inject
    private Logger log;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    @Inject
    private Identity identity;

    public String getResource() {
        String result = "";
        try {
            InputStream in = EmailSenderSubscriptionInvitation.class.getResourceAsStream("/email-template/sosNotification.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
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

    public void createEmailNotification(String emailList) {
        if (StringUtil.isEmpty(emailList)) {
            log.warn(" has not configured subscription invitation emails");
            return;
        }
        Notification notification = Notification.createNew();
        notification.setToAddress(emailList);
        notification.setSubject("Subscription invitation email ");
        notification.setContent("Subscription invitation email test");
        notification.setAccountId(identity.getIdentityDto().getAccountId());
        notification.setDeviceId(null);
        notification.setType(NotificationTypeEnum.EMAIL);
        notification.setStatus(PaymentStatusEnum.PENDING);
        notification.setCreatedDate(new Date());
        notificationService.add(notification);
    }

    private String getSubject(Position position) {
        StringBuilder subject = new StringBuilder();
        subject.append(labels.getMessage("fence.msg.device"));
        subject.append(" ");
        subject.append(position.getDevice().getName());
        subject.append(" ");
        subject.append(labels.getMessage("fence.msg.at.time"));
        subject.append(" ");
        subject.append(DateUtil.SDF_DETAIL.format(position.getTime()));
        return subject.toString();
    }

    private String getContent(Position position) {
        String content = getResource();
        content = rpl(content, "deviceName", position.getDevice().getName());
        content = rpl(content, "fence.message.device", labels.getMessage("fence.msg.device"));
        content = rpl(content, "sos.sent.msg", labels.getMessage("sos.sent.msg"));
        content = rpl(content, "fence.msg.at.time", labels.getMessage("msg.at.time"));
        content = rpl(content, "device.last.position", DateUtil.SDF_DETAIL.format(position.getTime()));
        content = rpl(content, "google.maps.url", "\n http:maps.google.com/maps?q=" + position.getLatitude() + "%2c" + position.getLongitude());
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        log.info("getContent: " + content);
        return content;
    }
}
