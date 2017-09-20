package ch.swissbytes.module.shared.notifications.push_notification.device.dropwatch;

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
import ch.swissbytes.module.shared.gcm.GcmContentModel;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class PushNotificationDropWatch implements Serializable {

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
        final String subject = labels.getMessage("alarm.drop.watch");
        final String content = getContent(device, alarm, viewer);
        notificationService.add(Notification.newForPushNotification(device, alarm, address, subject, content));
    }

    public String getContent(Device device, Alarm alarm, Viewer viewer) {
        GcmContentModel gcmContentModel = new GcmContentModel();
        gcmContentModel.addRegId(viewer.getGcmToken());
        gcmContentModel.createData(dataPushNotification(device, alarm));
        return new Gson().toJson(gcmContentModel);
    }

    public ViewerDto dataPushNotification(Device device, Alarm alarm) {
        ViewerDto viewerDto = ViewerDto.from(device, alarm);
        viewerDto.setTitle(labels.getMessage("low.battery.title"));
        viewerDto.setMessage(message(device));
        viewerDto.setNotificationType(PushNotificationType.ALERT_LOW_BATTERY);
        return viewerDto;
    }

    private String message(Device device) {
        return labels.getMessage("attention")
                + " " + labels.getMessage("the.watch.of")
                + " " + device.getName()
                + " " + labels.getMessage("was.removed.from.hand")
                + " " + labels.getMessage("in")
                + " " + labels.getMessage("low.battery.level");
    }
}
