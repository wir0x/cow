package ch.swissbytes.module.shared.timer.notification.worker;

import ch.swissbytes.module.buho.service.notifications.NotificationService;
import org.jboss.logging.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class WorkerNotificationSender {

    @Inject
    private Logger log;

    @Inject
    private NotificationService notificationService;

    private AtomicBoolean busy = new AtomicBoolean(false);

    @Lock(LockType.READ)
    public void doNotificationSender() throws InterruptedException {

        if (!busy.compareAndSet(false, true)) {
            return;
        }

        try {
            log.info("doNotificationSender started");
            notificationService.sendNotifications();
            log.info("doNotificationSender done");
        } finally {
            busy.set(false);
        }
    }
}
