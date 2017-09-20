package ch.swissbytes.module.shared.notifications.push_notification.geofence;

import ch.swissbytes.domain.dto.ViewerDto;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.FenceStatusEnum;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.gcm.GcmContentModel;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import com.google.gson.Gson;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class PushNotificationFence implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private AppConfiguration labels;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private NotificationService notificationService;


    @Inject
    private ViewerService viewerService;

    public void add(GeoFence geoFence, FenceStatusEnum fenceStatus, Position position) {
        final List<UserDevice> userDevices = userDeviceService.findByDeviceId(position.getDevice().getId());

        for (UserDevice userDevice : userDevices)
            notificationsToAppViewer(geoFence, fenceStatus, position, userDevice.getUser().getId());
    }

    private void notificationsToAppViewer(GeoFence geoFence, FenceStatusEnum fenceStatus, Position position, Long userId) {
        final List<Viewer> viewers = viewerService.findByUserId(userId);
        for (Viewer viewer : viewers)
            makeNotification(position, viewer, geoFence, fenceStatus);
    }

    private void makeNotification(Position position, Viewer viewer, GeoFence geoFence, FenceStatusEnum fenceStatusEnum) {
        Notification notification = Notification.createNew();
        notification.setToAddress(viewer.getImei());
        notification.setContent(getContent(position, viewer, geoFence, fenceStatusEnum));
        notification.setSubject(PushNotificationType.ALERT_FENCE.getName());
        notification.setAccountId(geoFence.getDevice().getAccount() != null ? geoFence.getDevice().getAccount().getId() : 0L);
        notification.setDeviceId(geoFence.getDevice().getId());
        notification.setType(NotificationTypeEnum.PUSH_NOTIFICATION);
        notification.setCreatedDate(position.getTime());
        notificationService.add(notification);
    }

    private String getContent(Position position, Viewer viewer, GeoFence geoFence, FenceStatusEnum fenceStatus) {
        GcmContentModel gcmContentModel = new GcmContentModel();
        gcmContentModel.addRegId(viewer.getGcmToken());

        ViewerDto viewerPushNotificationDto = ViewerDto.from(position);
        viewerPushNotificationDto.setTitle(labels.getMessage("fence.title"));
        viewerPushNotificationDto.setMessage(message(position, fenceStatus, geoFence));
        viewerPushNotificationDto.setFenceId(geoFence.getId().toString());
        viewerPushNotificationDto.setFenceName(geoFence.getName());
        viewerPushNotificationDto.setNotificationType(PushNotificationType.ALERT_FENCE);
        gcmContentModel.createData(viewerPushNotificationDto);
        return new Gson().toJson(gcmContentModel);
    }

    private String message(Position position, FenceStatusEnum fenceStatus, GeoFence geoFence) {
        return labels.getMessage("fence.message")
                + " " + position.getDevice().getName()
                + " " + getStatus(fenceStatus)
                + " " + geoFence.getName();
    }

    private String getStatus(FenceStatusEnum fenceStatus) {
        return fenceStatus == FenceStatusEnum.ENTER ? labels.getMessage("fence.enter") : labels.getMessage("fence.exit");
    }
}
