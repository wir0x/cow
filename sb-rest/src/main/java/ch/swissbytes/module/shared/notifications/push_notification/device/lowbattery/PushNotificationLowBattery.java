package ch.swissbytes.module.shared.notifications.push_notification.device.lowbattery;

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

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class PushNotificationLowBattery implements Serializable {

    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;
    @Inject
    private ViewerService viewerService;


    public void add(Device device, Alarm alarm) {
        final List<UserDevice> userDeviceList = userDeviceService.findByDeviceId(device.getId());

        for (UserDevice userDevice : userDeviceList)
            notificationsToAppViewer(device, alarm, userDevice.getUser().getId());
    }

    private void notificationsToAppViewer(Device device, Alarm alarm, Long userId) {
        final List<Viewer> viewerList = viewerService.findByUserId(userId);
        for (Viewer viewer : viewerList)
            makeNotification(device, alarm, viewer);
    }

    private void makeNotification(Device device, Alarm alarm, Viewer viewer) {
        final String address = viewer.getImei();
        final String subject = labels.getMessage("alarm.low.battery");
        final String content = getContent(device, alarm, viewer);

        notificationService.add(Notification.newForPushNotification(device, alarm, address, subject, content));
    }

    public String getContent(Device device, Alarm alarm, Viewer viewer) {
        final ViewerDto data = pnDtoData(device, alarm);
        return PushNotificationHelper.makeContent(viewer, data);
    }

    private ViewerDto pnDtoData(Device device, Alarm alarm) {
        ViewerDto dto = ViewerDto.from(device, alarm);
        dto.setTitle(labels.getMessage("low.battery.title"));
        dto.setMessage(message(device, alarm));
        dto.setNotificationType(PushNotificationType.ALERT_LOW_BATTERY);
        return dto;
    }

    private String message(Device device, Alarm alarm) {
        return labels.getMessage("low.battery.message")
                + " " + device.getName()
                + " " + labels.getMessage("low.battery.remaining")
                + " " + alarm.getBattery()
                + " " + labels.getMessage("low.battery.level");
    }
}
