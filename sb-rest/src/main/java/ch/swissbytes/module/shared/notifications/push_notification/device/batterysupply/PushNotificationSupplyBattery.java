package ch.swissbytes.module.shared.notifications.push_notification.device.batterysupply;

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
public class PushNotificationSupplyBattery implements Serializable {

    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;
    @Inject
    private ViewerService viewerService;


    public void addConnected(Device device, Alarm alarm) {
        final List<UserDevice> userDevices = userDeviceService.findByDeviceId(device.getId());
        final String subject = labels.getMessage("alarm.external.battery.connected");

        for (UserDevice userDevice : userDevices)
            notificationsToAppViewer(device, alarm, userDevice.getUser().getId(), subject);
    }

    public void addDisconnected(Device device, Alarm alarm) {
        final List<UserDevice> userDevices = userDeviceService.findByDeviceId(device.getId());
        final String subject = labels.getMessage("alarm.external.battery.disconnected");

        for (UserDevice userDevice : userDevices)
            notificationsToAppViewer(device, alarm, userDevice.getUser().getId(), subject);
    }

    private void notificationsToAppViewer(Device device, Alarm alarm, Long userId, String subject) {
        final List<Viewer> viewers = viewerService.findByUserId(userId);
        for (Viewer viewer : viewers)
            makeNotification(device, alarm, viewer, subject);
    }

    private void makeNotification(Device device, Alarm alarm, Viewer viewer, String subject) {
        final String address = viewer.getImei();
        final String content = getContent(device, alarm, viewer, subject);

        notificationService.add(Notification.newForPushNotification(device, alarm, address, subject, content));
    }

    private String getContent(Device device, Alarm alarm, Viewer viewer, String subject) {
        final ViewerDto data = dtoData(device, alarm, subject);
        return PushNotificationHelper.makeContent(viewer, data);
    }

    private ViewerDto dtoData(Device device, Alarm alarm, String subject) {
        ViewerDto dto = ViewerDto.from(device, alarm);
        final String supplyBattery = powerSupplyBySubject(subject);
        dto.setTitle(labels.getMessage("supply.battery.title") + " ");
        dto.setMessage(getMessage(device, supplyBattery));
        dto.setNotificationType(PushNotificationType.ALERT_DISCONNECT_BATTERY);
        return dto;
    }

    private String powerSupplyBySubject(String subject) {
        final String connected = labels.getMessage("supply.battery.connected");
        final String disconnected = labels.getMessage("supply.battery.disconnect");
        return subject.equals(connected) ? connected : disconnected;
    }

    private String getMessage(Device device, String supplyBattery) {
        return labels.getMessage("supply.battery.message")
                + " " + device.getName()
                + " " + supplyBattery;
    }
}
