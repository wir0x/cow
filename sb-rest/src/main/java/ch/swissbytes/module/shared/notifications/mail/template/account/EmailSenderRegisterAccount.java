package ch.swissbytes.module.shared.notifications.mail.template.account;

import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.utils.AppConfiguration;
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
public class EmailSenderRegisterAccount implements Serializable {

    @Inject
    private Logger log;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    public String getResource() {
        String result = "";
        try {
            InputStream in = EmailSenderRegisterAccount.class.getResourceAsStream("/email-template/RegisterAccountNotification.html");
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

    public void createEmailNotification(User user) {
        Notification notification = Notification.createNew();
        notification.setToAddress(user.getEmail());
        notification.setSubject(getSubject(user));
        notification.setContent(getContent(user));
        notification.setAccountId(user.getAccount().getId());
        notification.setType(NotificationTypeEnum.EMAIL);
        notification.setStatus(PaymentStatusEnum.PENDING);
        notification.setCreatedDate(new Date());
        notificationService.add(notification);
    }

    private String getSubject(User user) {
        StringBuilder subject = new StringBuilder();
        subject.append(user.getAccount().getName());
        subject.append(" ");
        subject.append(labels.getMessage("msg.welcome.buho"));
        subject.append(" ");
        subject.append(user.getAccount().getName());
        subject.append(" ");
        return subject.toString();
    }

    private String getContent(User user) {
        String content = getResource();
        content = rpl(content, "hi", labels.getMessage("msg.hi"));
        content = rpl(content, "accountName", user.getAccount().getName());
        content = rpl(content, "thank.register", labels.getMessage("msg.thanks.register"));
        content = rpl(content, "downloadOurApps", labels.getMessage("msg.download.our.apss"));
        content = rpl(content, "appViewer", labels.getMessage("msg.app.viewer"));
        content = rpl(content, "urlAppViewer", labels.getMessage("msg.url.app.viewer"));
        content = rpl(content, "appTracker", labels.getMessage("msg.app.tracker"));
        content = rpl(content, "urlAppTracker", labels.getMessage("msg.url.app.tracker"));
        content = rpl(content, "remember", labels.getMessage("msg.remember"));
        content = rpl(content, "moreInfo", labels.getMessage("msg.more.info"));
        content = rpl(content, "urlInfo", labels.getMessage("msg.url.buho"));
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        content = rpl(content, "CurrentDate", getYear());

        log.info("content: " + content);
        return content;
    }
}
