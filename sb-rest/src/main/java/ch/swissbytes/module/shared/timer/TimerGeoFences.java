package ch.swissbytes.module.shared.timer;

import ch.swissbytes.module.buho.service.AlarmsFencesService;
import ch.swissbytes.module.buho.service.IGeoFenceService;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

@Startup
@Singleton
public class TimerGeoFences {

    @Inject
    private Logger log;
    @Inject
    private IGeoFenceService geoFenceService;

    @Resource
    private TimerService timerService;

    @Inject
    private AlarmsFencesService alarmsFencesService;

    @PostConstruct
    public void startTimer() {
        timerService.getTimers().forEach(Timer::cancel);

        int seconds = 30; // 30 seconds

        TimerConfig tc = new TimerConfig();
        tc.setPersistent(false);
        timerService.createIntervalTimer(0, seconds * 1000, tc);
    }

    @Timeout
    public void timeout(Timer timer) {
        final long started = System.currentTimeMillis();
        log.info("Timer GeoFence... stared");

//        geoFenceService.sendNotification();
//        alarmsFencesService.findFencesAndMakeNotification();

        final long finished = System.currentTimeMillis();
        log.info("Timer GeoFence... finished []" + (finished - started));
    }

}
