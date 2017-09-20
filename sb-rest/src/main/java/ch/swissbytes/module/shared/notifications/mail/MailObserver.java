package ch.swissbytes.module.shared.notifications.mail;

import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.notifications.mail.configuration.Mail;
import ch.swissbytes.module.shared.notifications.mail.configuration.MailSender;
import org.jboss.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class MailObserver {

    @Inject
    private Logger logger;
    @Inject
    private NotificationService notificationService;

    @Asynchronous
    public void sendMail(Mail fields) {
        MailSender baseMailer = new MailSender(fields);
        baseMailer.sendEmail();
    }

    @Asynchronous
    public void send(Notification notification) {
        logger.info("notification: " + notification.getId());
        MailSender mailSender = new MailSender();
        notification = mailSender.sendEmail(notification);
        notificationService.update(notification);
    }
}
