package ch.swissbytes.module.shared.notifications.sms.template.geofence;

import ch.swissbytes.domain.enumerator.FenceStatusEnum;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;

@Stateless
public class SmsGeoFenceSender implements Serializable {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public void add(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        final String sms = geoFence.getCellphones();
        final Account account = geoFence.getDevice().getAccount();

        if (StringUtil.isNotEmpty(sms) && EntityUtil.isNotNull(account))
            makeNotification(geoFence, fenceStatus, lastPosition);
    }

    private void makeNotification(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        final String[] cellphones = geoFence.getCellphones().split("(;)|(\\,)|(\\r?\\n)");

        for (String cellphone : cellphones) {
            try {
                Notification notification = Notification.createNew();
                notification.setToAddress(cellphone);
                notification.setContent(getContent(geoFence, fenceStatus));
                notification.setAccountId(geoFence.getDevice().getAccount().getId());
                notification.setDeviceId(geoFence.getDevice().getId());
                notification.setType(NotificationTypeEnum.SMS);
                notification.setCreatedDate(lastPosition.getTime());
                notificationService.add(notification);
            } catch (InvalidInputException e) {
                log.warn(e);
            }
        }
    }

    public String getContent(GeoFence geoFence, FenceStatusEnum fenceStatus) {
        StringBuilder bodyMessage = new StringBuilder();
        bodyMessage.append(labels.getMessage("fence.msg.device"));
        bodyMessage.append(" ");
        bodyMessage.append(geoFence.getDevice().getName());
        bodyMessage.append(" ");
        bodyMessage.append(FenceStatusEnum.ENTER == fenceStatus ? labels.getMessage("fence.msg.is.entering") : labels.getMessage("fence.msg.is.leaving"));
        bodyMessage.append(" ");
        bodyMessage.append(geoFence.getName());
        bodyMessage.append(" ");
        return bodyMessage.toString();
    }
}
