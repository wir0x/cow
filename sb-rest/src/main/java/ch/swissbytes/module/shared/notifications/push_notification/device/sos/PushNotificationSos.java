package ch.swissbytes.module.shared.notifications.push_notification.device.sos;

import ch.swissbytes.domain.dto.ViewerDto;
import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.notifications.push_notification.PushNotificationHelper;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class PushNotificationSos implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;
    @Inject
    private ViewerService viewerService;


    public void add(Device device, Alarm alarm) {
        final List<UserDevice> userDevices = userDeviceService.findByDeviceId(device.getId());

        for (UserDevice userDevice : userDevices)
            notificationsToAppViewer(device, alarm, userDevice.getUser().getId());
    }

    private void notificationsToAppViewer(Device device, Alarm alarm, Long userId) {
        final List<Viewer> viewers = viewerService.findByUserId(userId);
        for (Viewer viewer : viewers)
            makeNotification(device, alarm, viewer);
    }

    private void makeNotification(Device device, Alarm alarm, Viewer viewer) {
        final String address = viewer.getImei();
        final String subject = labels.getMessage("alarm.sos");
        final String content = getContent(device, alarm, viewer);

        notificationService.add(Notification.newForPushNotification(device, alarm, address, subject, content));
        log.info("SOS Push Notification created!");
    }

    public String getContent(Device device, Alarm alarm, Viewer viewer) {
        final ViewerDto data = pnDtoData(device, alarm);
        return PushNotificationHelper.makeContent(viewer, data);
    }

    private ViewerDto pnDtoData(Device device, Alarm alarm) {
        ViewerDto dto = ViewerDto.from(device, alarm);
        dto.setTitle(labels.getMessage("alarm.sos"));
        dto.setMessage(message(device, alarm.isSosOldPosition()));
        dto.setNotificationType(PushNotificationType.ALERT_SOS);
        return dto;
    }

    private String message(Device device, boolean isSosPositionOld) {
        String oldPosition = isSosPositionOld ? labels.getMessage("sos.position.old") : " ";
        return labels.getMessage("sos.message")
                + " " + device.getName()
                + " " + labels.getMessage("sos.send.sos")
                + " " + oldPosition;
    }
}
