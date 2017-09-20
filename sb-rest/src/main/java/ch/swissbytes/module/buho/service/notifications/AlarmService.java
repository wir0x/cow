package ch.swissbytes.module.buho.service.notifications;

import ch.swissbytes.domain.dao.AlarmDao;
import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.service.notifications.device.DropWatchAlertService;
import ch.swissbytes.module.buho.service.notifications.device.LowBatteryAlertService;
import ch.swissbytes.module.buho.service.notifications.device.SosAlertService;
import ch.swissbytes.module.buho.service.notifications.device.SupplyBatteryAlertService;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class AlarmService {

    @Inject
    private Logger log;
    @Inject
    private AlarmDao alarmDao;
    @Inject
    private SosAlertService sosAlertService;
    @Inject
    private LowBatteryAlertService lowBatteryAlertService;
    @Inject
    private DropWatchAlertService dropWatchAlertService;
    @Inject
    private SupplyBatteryAlertService supplyBatteryAlertService;


    public void createAlarmNotification() {

        List<Alarm> alarms = alarmDao.findPending();
        log.info("PENDING alarms: " + alarms.size());

        for (Alarm alarm : alarms) {
            closeAlarm(alarm);

            switch (alarm.getType()) {
                case "DROP":
                    dropWatchAlertService.notification(alarm);
                    break;
                case "GTBPL":
                    lowBatteryAlertService.notification(alarm);
                    break;
                case "GTMPN":
                    supplyBatteryAlertService.notificationConnected(alarm);
                    break;
                case "GTMPF":
                    supplyBatteryAlertService.notificationDisconnected(alarm);
                    break;
                case "GTSOS":
                    sosAlertService.notification(alarm);
                    break;
                case "EMSMS":
                    sosAlertService.notification(alarm);
                    break;
            }
        }
    }

    private void closeAlarm(Alarm alarm) {
        alarm.setSend(true);
        alarm.setDateSent(new Date());
        alarmDao.update(alarm);
    }

    public Alarm addByPosition(Position position) {
        Alarm alarm = Alarm.createSOSFromPosition(position);
        return alarmDao.save(alarm);
    }
}
