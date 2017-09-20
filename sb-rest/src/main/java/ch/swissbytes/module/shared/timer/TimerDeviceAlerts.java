package ch.swissbytes.module.shared.timer;

import ch.swissbytes.module.buho.service.notifications.AlarmService;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

@Startup
@Singleton
public class TimerDeviceAlerts {

    @Inject
    private Logger log;
    @Inject
    private AlarmService alarmService;

    @Resource
    private TimerService timerService;


    @PostConstruct
    public void startTimer() {
        timerService.getTimers().forEach(Timer::cancel);

        int seconds = 25;

        TimerConfig tc = new TimerConfig();
        tc.setPersistent(false);
        timerService.createIntervalTimer(0, seconds * 1000, tc);
    }

    @Timeout
    public void timeout(Timer timer) {
        log.info("Timer Device alerts... started");
        final long started = System.currentTimeMillis();

//        alarmService.createAlarmNotification();

        final long finished = System.currentTimeMillis();
        log.info("Timer Device alerts... finished [] " + (finished - started));
    }

}
