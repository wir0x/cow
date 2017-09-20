package ch.swissbytes.module.shared.gcm.observer;

import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.shared.gcm.NotificationSender;
import org.jboss.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PushNotificationObserver {

    @Inject
    private Logger logger;
    @Inject
    private NotificationSender notificationSender;


    @Asynchronous
    public void send(Notification notification) {
        notificationSender.sendNotification(notification);
    }
}
