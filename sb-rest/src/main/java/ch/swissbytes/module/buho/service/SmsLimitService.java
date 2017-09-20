package ch.swissbytes.module.buho.service;

import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.notifications.mail.MailObserver;
import ch.swissbytes.module.shared.notifications.mail.template.device.sos.MailSosAlert;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

@Stateless
public class SmsLimitService {
    @Inject
    private Logger log;

    @Inject
    private AccountService accountService;

    @Inject
    private MailObserver mailObserver;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    @Inject
    private UserRepository userRepository;


    public Boolean createSmsLimitNotification(SmsControl smsControl, Notification notification_) {
        log.info("createSmsLimitNotification " + smsControl + " | " + notification_.getDeviceId());

        long limit_sms = KeyAppConfiguration.getLong(ConfigurationKey.SMS_LIMIT_PERCENTAGE);
        double limitPercentage = (smsControl.getMaxSms() * limit_sms) / 100;

        try {
            if (smsControl.getUsedSms() >= limitPercentage && !smsControl.getSentMail()) {
                sendMailToAllAdmins(notification_, smsControl);
                return true;
            }
        } catch (InvalidInputException e) {
            log.error(e.getMessage(), e);
            notification_.setStatus(PaymentStatusEnum.ERROR);
            notification_.setErrorDescription(e.getMessage());
        }
        return false;
    }

    private void sendMailToAllAdmins(Notification notification, SmsControl smsControl) {
        log.info("sendMailToAllAdmins " + notification + "\n" + smsControl);
        List<User> userList = userRepository.findAccountAdmin(notification.getAccountId());

        try {
            for (User user : userList) {
                log.info("sendMail to admin " + user.getEmail());
                mailObserver.send(createMailLimitNotification(notification, user, smsControl));
            }

        } catch (InvalidInputException e) {
            log.error(e.getMessage(), e);
            notification.setStatus(PaymentStatusEnum.ERROR);
            notification.setErrorDescription(e.getMessage());
        }
    }

    private Notification createMailLimitNotification(Notification notification_, User user, SmsControl smsControl) {
        log.info("createMailLimitNotification ");
        Notification notification = Notification.createNew();
        notification.setToAddress(user.getEmail());
        notification.setSubject(getSubject(smsControl));
        notification.setContent(getContent(smsControl));
        notification.setAccountId(notification_.getAccountId());
        notification.setDeviceId(notification_.getDeviceId());
        notification.setType(NotificationTypeEnum.EMAIL);
        notification.setStatus(PaymentStatusEnum.PENDING);
        notification.setCreatedDate(new Date());
        return notification;
    }

    public String getResource() {
        String result = "";
        try {
            InputStream in = MailSosAlert.class.getResourceAsStream("/email-template/limitSMSNotification.html");
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

    private String getSubject(SmsControl smsControl) {
        StringBuilder subject = new StringBuilder();
        subject.append(labels.getMessage("fence.msg.device"));
        subject.append(" ");
        subject.append(smsControl.getDevice().getName());
        subject.append(" ");
        return subject.toString();
    }

    private String getContent(SmsControl smsControl) {
        String content = getResource();
        content = rpl(content, "deviceName", smsControl.getDevice().getName());
        content = rpl(content, "fence.message.device", labels.getMessage("fence.msg.device"));
        content = rpl(content, "sms.msg.limit", labels.getMessage("sms.msg.limit"));
        content = rpl(content, "sms.msg.limit1", labels.getMessage("sms.msg.limit1"));
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        log.info("getContent: " + content);
        return content;
    }
}
