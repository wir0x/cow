package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.PaymentPushNotificationDto;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.gcm.GcmContentModel;
import ch.swissbytes.module.shared.gcm.NotificationSender;
import ch.swissbytes.module.shared.utils.AppConfiguration;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class GcmControlService {

    @Inject
    private UserDeviceRepository userDeviceRepository;

    @Inject
    private NotificationService notificationService;

    @Inject
    private AppConfiguration labels;

    @Inject
    private ViewerDao viewerDao;

    void sendNotification(PaymentPushNotificationDto pushDto) {
        GcmContentModel gcmContentModel = new GcmContentModel();
        gcmContentModel.addRegId(pushDto.getToken());
        gcmContentModel.createData(pushDto.getTitle(), pushDto.getMessage(), pushDto.getNotificationType(),
                pushDto.getInnerData(), pushDto.getErrorType());
        NotificationSender sender = new NotificationSender(gcmContentModel);
        sender.send();
    }
}
