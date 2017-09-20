package ch.swissbytes.module.shared.notifications.mail.template.subscription;

import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;

@Stateless
public class MailSenderSubscriptionAlert implements Serializable {

    @Inject
    private Logger log;

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AppConfiguration labels;

    public String getResource() {
        String result = "";
        try {
            InputStream in = MailSenderSubscriptionAlert.class.getResourceAsStream("/email-template/subscriptionNotification.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
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

    public void createEmailNotification(Device device, int remainingDays) {
        final Account account = device.getAccount();

        if (EntityUtil.isNotNull(account) && StringUtil.isNotEmpty(device.getAccount().getEmail()))
            makeNotification(device, remainingDays);
    }

    private void makeNotification(Device device, int remainingDays) {
        Notification notification = Notification.createNew();
        notification.setToAddress(device.getAccount().getEmail());
        notification.setSubject(getSubject(device));
        notification.setContent(getContent(device, remainingDays, device.getAccount().getName()));
        notification.setAccountId(device.getAccount().getId());
        notification.setType(NotificationTypeEnum.EMAIL);
        notificationService.add(notification);
    }

    private String getSubject(Device device) {
        StringBuilder subject = new StringBuilder();
        subject.append(labels.getMessage("msg.lbl.subscription"));
        subject.append(" ");
        subject.append(device.getName());
        subject.append(" ");
        return subject.toString();
    }

    private String getContent(Device device, int remainingDays, String nameOwner) {
        String content = getResource();
        content = rpl(content, "cnt.lbl.subscription", labels.getMessage("msg.lbl.subscription"));
        content = rpl(content, "cnt.device.name", device.getName());
        content = rpl(content, "cnt.there", labels.getMessage("msg.lbl.there"));
        content = rpl(content, "cnt.owner.account", nameOwner);
        content = rpl(content, "cnt.info.subscription", labels.getMessage("msg.info.subscription.device"));
        content = rpl(content, "cnt.finally.time", labels.getMessage("msg.finally.time"));
        content = rpl(content, "cnt.communication", labels.getMessage("msg.communication"));
        content = rpl(content, "cnt.pd.actually", labels.getMessage("msg.pd.actually"));
        content = rpl(content, "cnt.days.finally", String.valueOf(remainingDays));
        content = rpl(content, "cnt.finally.subscription", labels.getMessage("msg.days.finally.subscription"));
        content = rpl(content, "cnt.thank.you", labels.getMessage("msg.thank.you"));
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
