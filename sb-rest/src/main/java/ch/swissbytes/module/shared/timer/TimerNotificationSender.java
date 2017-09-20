package ch.swissbytes.module.shared.timer;

//@Startup
//@Singleton
public class TimerNotificationSender {

//    @Inject
//    private Logger log;
//    @Inject
//    private NotificationService notificationService;
//
//    @Resource
//    private TimerService timerService;
//
//    @PostConstruct
//    public void startTimer() {
//        timerService.getTimers().forEach(Timer::cancel);
//
//        int seconds = 30;
//
//        TimerConfig tc = new TimerConfig();
//        tc.setPersistent(false);
//        timerService.createIntervalTimer(0, seconds * 1000, tc);
//    }
//
//    @Timeout
//    public void timeout(Timer timer) {
//        final long started = System.currentTimeMillis();
//        log.info("Timer Notification sender... started");
//
//        notificationService.sendNotifications();
//
//        final long finished = System.currentTimeMillis();
//        log.info("Timer Notification sender... finished " + (finished - started));
//    }
}
