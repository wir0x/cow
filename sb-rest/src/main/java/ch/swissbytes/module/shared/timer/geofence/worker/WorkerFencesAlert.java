package ch.swissbytes.module.shared.timer.geofence.worker;

import ch.swissbytes.module.buho.service.AlarmsFencesService;
import org.jboss.logging.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class WorkerFencesAlert {

    @Inject
    private Logger log;

    @Inject
    private AlarmsFencesService alarmsFencesService;

    private AtomicBoolean busy = new AtomicBoolean(false);

    @Lock(LockType.READ)
    public void doFencesAlert() throws InterruptedException {

        if (!busy.compareAndSet(false, true)) {
            return;
        }

        try {
            log.info("doFencesAlert started");
            alarmsFencesService.findFencesAndMakeNotification();
            log.info("doFencesAlert done");
        } finally {
            busy.set(false);
        }
    }
}
