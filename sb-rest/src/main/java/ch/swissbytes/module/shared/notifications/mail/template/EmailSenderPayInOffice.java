package ch.swissbytes.module.shared.notifications.mail.template;

import ch.swissbytes.domain.dto.CustomerDto;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
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
public class EmailSenderPayInOffice implements Serializable {

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
            InputStream in = EmailSenderSubscriptionInvitation.class.getResourceAsStream("/email-template/PayInOfficeNotification.html");
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

    public void createEmailNotification(CustomerDto customerDto) throws InvalidInputException {
        String emailBuho = KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_ADDRESS_SUPPORT);
        Notification notification = Notification.createNew();
        notification.setToAddress(emailBuho);
        notification.setSubject(getSubject());
        notification.setContent(getContent(customerDto));
        notificationService.add(notification);
    }

    private String getSubject() {
        return labels.getMessage("mgs.user.pay.in.office") + " ";
    }

    private String getContent(CustomerDto customerDto) {
        String content = getResource();
        content = rpl(content, "customerName", customerDto.getName());
        content = rpl(content, "phoneNumber", customerDto.getPhoneNumber());
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        log.info("getContent: " + content);
        return content;
    }
}
