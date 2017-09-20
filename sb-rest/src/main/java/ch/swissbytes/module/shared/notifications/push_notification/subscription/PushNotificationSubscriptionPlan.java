package ch.swissbytes.module.shared.notifications.push_notification.subscription;

import ch.swissbytes.domain.dto.ViewerDto;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.domain.enumerator.PushNotificationType;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.gcm.GcmContentModel;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.StringUtil;
import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class PushNotificationSubscriptionPlan implements Serializable {

    @Inject
    private UserDeviceService userDeviceService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    @Inject
    private ViewerService viewerService;

    public void createPushNotification(Device device, int expiredSubscription) {
        final List<UserDevice> userDevices = userDeviceService.findByDeviceId(device.getId());

        if (StringUtil.isNotEmpty(device.getImei()) && StringUtil.isNotEmpty(device.getGcmToken()))
            createNotification(device, device.getImei(), device.getGcmToken(), expiredSubscription);

        for (UserDevice userDevice : userDevices) {
            final List<Viewer> viewerList = viewerService.findByUserId(userDevice.getUser().getId());
            for (Viewer viewer : viewerList)
                createNotification(device, viewer.getImei(), viewer.getGcmToken(), expiredSubscription);
        }
    }

    private void createNotification(Device device, String uniqueId, String gcmToken, int expiredSubscription) {
        Notification notification = Notification.createNew();
        notification.setToAddress(uniqueId);
        notification.setContent(getContent(device, gcmToken, expiredSubscription));
        notification.setSubject(PushNotificationType.ALERT_SUBSCRIPTION.getName());
        notification.setAccountId(device.getAccount().getId());
        notification.setDeviceId(device.getId());
        notification.setType(NotificationTypeEnum.PUSH_NOTIFICATION);
        notificationService.add(notification);
    }

    private String getContent(Device device, String gmcToken, int daysExpiredSubscription) {
        final ViewerDto dto = createData(device, daysExpiredSubscription);
        GcmContentModel gcmContentModel = new GcmContentModel();
        gcmContentModel.addRegId(gmcToken);
        gcmContentModel.createData(dto);
        return new Gson().toJson(gcmContentModel);
    }

    private ViewerDto createData(Device device, int daysExpiredSubscription) {
        ViewerDto viewerDto = ViewerDto.createNew();
        viewerDto.setTitle(labels.getMessage("subscription.title"));
        viewerDto.setMessage(labels.getMessage("subscription.message") + " "
                + device.getName() + " "
                + labels.getMessage("subscription.expires") + " "
                + daysExpiredSubscription + " "
                + labels.getMessage("subscription.expires.day"));
        viewerDto.setNotificationType(PushNotificationType.ALERT_SUBSCRIPTION);
        return viewerDto;
    }
}
