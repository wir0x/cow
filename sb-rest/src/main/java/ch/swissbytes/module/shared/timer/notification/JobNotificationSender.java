package ch.swissbytes.module.shared.timer.notification;

import ch.swissbytes.module.shared.timer.notification.worker.WorkerNotificationSender;
import org.jboss.logging.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class JobNotificationSender {

    @Inject
    private Logger log;

    @Inject
    private WorkerNotificationSender workerNotificationSender;

    @Lock(LockType.READ)
    @Schedule(hour = "*", minute = "*", second = "*/30", info = "Every 30 seconds", persistent = false)
    public void scheduleNotificationSenderNotification() {
        log.info("scheduleNotificationSender");
        try {
            workerNotificationSender.doNotificationSender();
        } catch (InterruptedException e) {
            log.error("Error background job  " + e.getMessage(), e);
        }
    }
}
