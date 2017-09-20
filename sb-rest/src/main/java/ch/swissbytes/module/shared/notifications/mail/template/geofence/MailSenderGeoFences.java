package ch.swissbytes.module.shared.notifications.mail.template.geofence;

import ch.swissbytes.domain.enumerator.FenceStatusEnum;
import ch.swissbytes.domain.enumerator.NotificationTypeEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

@Stateless
public class MailSenderGeoFences {

    @Inject
    private Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private AppConfiguration labels;

    public String getResource() {
        String result = "";
        try {
            InputStream in = MailSenderGeoFences.class.getResourceAsStream("/email-template/geoFencesNotification.html");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    protected String rpl(String string, String emailKey, String msgKey) {
        return (string.indexOf(emailKey) > 0) ? string.replace("{" + emailKey + "}", msgKey) : string;
    }

    public String getYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public void add(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        final String mails = geoFence.getEmails();
        final Account account = geoFence.getDevice().getAccount();

        if (StringUtil.isNotEmpty(mails) && EntityUtil.isNotNull(account))
            makeNotification(geoFence, fenceStatus, lastPosition);
    }

    private void makeNotification(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        Notification notification = Notification.createNew();
        notification.setToAddress(geoFence.getEmails());
        notification.setSubject(getSubject(geoFence, fenceStatus, lastPosition));
        notification.setContent(getContent(geoFence, fenceStatus, lastPosition));
        notification.setAccountId(geoFence.getDevice().getAccount().getId());
        notification.setDeviceId(geoFence.getDevice().getId());
        notification.setType(NotificationTypeEnum.EMAIL);
        notification.setCreatedDate(lastPosition.getTime());
        notificationService.add(notification);
    }

    private String getSubject(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        StringBuilder subject = new StringBuilder();
        subject.append(labels.getMessage("fence.msg.device"));
        subject.append(" ");
        subject.append(geoFence.getDevice().getName());
        subject.append(" ");
        subject.append(FenceStatusEnum.ENTER == fenceStatus ? labels.getMessage("fence.msg.is.entering") : labels.getMessage("fence.msg.is.leaving"));
        subject.append(" ");
        subject.append(geoFence.getName());
        subject.append(" ");
        subject.append(labels.getMessage("fence.msg.at.time"));
        subject.append(" ");
        subject.append(DateUtil.SDF_DETAIL.format(lastPosition.getTime()));
        return subject.toString();
    }

    public String getContent(GeoFence geoFence, FenceStatusEnum fenceStatus, Position lastPosition) {
        String content = getResource();
        content = rpl(content, "deviceName", geoFence.getDevice().getName());
        content = rpl(content, "fence.message.device", labels.getMessage("fence.msg.device"));
        content = rpl(content, "fence.status.msg", fenceStatus != FenceStatusEnum.ENTER ? labels.getMessage("fence.msg.is.leaving") : labels.getMessage("fence.msg.is.entering"));
        content = rpl(content, "fence.name", geoFence.getName());
        content = rpl(content, "fence.msg.at.time", labels.getMessage("fence.msg.at.time"));
        content = rpl(content, "device.last.position", DateUtil.SDF_DETAIL.format(lastPosition.getTime()));
        content = rpl(content, "google.maps.url", "\n http:maps.google.com/maps?q=" + lastPosition.getLatitude() + "%2c" + lastPosition.getLongitude());
        content = rpl(content, "CurrentDate", getYear());
        content = rpl(content, "imageLogo", "https://app.buho.bo/img/logo-login.png");
        return content;
    }
}
