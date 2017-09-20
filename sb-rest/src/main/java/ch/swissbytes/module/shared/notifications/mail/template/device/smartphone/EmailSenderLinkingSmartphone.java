package ch.swissbytes.module.shared.notifications.mail.template.device.smartphone;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.notifications.mail.template.subscription.EmailSenderSubscriptionInvitation;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.MailUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;

@Stateless
public class EmailSenderLinkingSmartphone implements Serializable {

    @Inject
    private Logger log;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    @Inject
    private Identity identity;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private MailUtil mailUtil;

    public String getResource() {
        String result = "";
        try {
            InputStream in = EmailSenderSubscriptionInvitation.class.getResourceAsStream("/email-template/LinkingSmartphoneNotification.html");
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

    public void createEmailNotification(String email, Device device) throws InvalidInputException {
        if (!mailUtil.validate(email)) {
            throw new InvalidInputException("email not valid");
        }

        Notification notification = Notification.createNew();

        notification.setToAddress(email);
        notification.setSubject(getSubject());
        notification.setContent(getContent(device));

        notificationService.add(notification);
    }

    private String getSubject() {
        return labels.getMessage("msg.invitation.title") + " ";
    }

    private String getContent(Device device) {
        String content = getResource();
        content = rpl(content, "generatedId", device.getGeneratedId());
//        content = rpl(content, "dear", labels.getMessage("msg.invitation.dear"));
//        content = rpl(content, "userName", subscription.getUser().getName());
//        content = rpl(content, "accountName", subscription.getUser().getAccount().getName());
//        content = rpl(content, "message1", labels.getMessage("msg.invitation.msg1"));
//        content = rpl(content, "urlTracker", labels.getMessage("msg.invitation.url"));
//        content = rpl(content, "userAndPassword", labels.getMessage("msg.invitation.user.password"));
//        content = rpl(content, "user", labels.getMessage("msg.invitation.user"));
//        content = rpl(content, "username", subscription.getUser().getUsername());
//        content = rpl(content, "pass", labels.getMessage("msg.invitation.pass"));
//        content = rpl(content, "password", subscription.getUser().getGeneratedPassword());
//        content = rpl(content, "thanks1", labels.getMessage("msg.invitation.thanks1"));
//        content = rpl(content, "thanks2", labels.getMessage("msg.invitation.thanks2"));
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        log.info("getContent: " + content);
        return content;
    }
}
