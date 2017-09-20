package ch.swissbytes.module.shared.notifications.sms.configuration;

import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import org.jboss.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;

@Stateless
public class SmsObserver implements Serializable {

    @Inject
    private Logger logger;
    @Inject
    private SmsSender smsSender;
    @Inject
    private NotificationService notificationService;


    @Asynchronous
    public void send(Notification notification) {
        notification = smsSender.sendSms(notification);
        notificationService.update(notification);
    }
}
