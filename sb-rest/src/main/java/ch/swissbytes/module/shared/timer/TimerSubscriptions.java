package ch.swissbytes.module.shared.timer;

import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

@Startup
@Singleton
public class TimerSubscriptions {

    @Inject
    private Logger log;
    @Inject
    private SubscriptionService subscriptionService;

    @Resource
    private TimerService timerService;


    @PostConstruct
    public void startTimer() {
        timerService.getTimers().forEach(Timer::cancel);

        TimerConfig tc = new TimerConfig();
        tc.setPersistent(false);

        ScheduleExpression se = scheduleConfiguration();

        timerService.createCalendarTimer(se, tc);
    }

    @Timeout
    public void timeout(Timer timer) {
        log.info("Timer subscriptions... stared");
//        subscriptionService.createSubscriptionNotifications();
        log.info("Timer subscriptions... finished");
    }

    private ScheduleExpression scheduleConfiguration() {
        ScheduleExpression se = new ScheduleExpression();
        se.hour(9);
        se.minute(30);
        return se;
    }
}
