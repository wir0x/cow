package ch.swissbytes.module.shared.gcm;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.buho.service.notifications.NotificationService;
import com.google.gson.Gson;
import org.jboss.logging.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
public class NotificationSender {

    private final String API_KEY = "AIzaSyAXYUg5X9ziB4WWAjuY32zaI6Jvw3MKkc0";
    private final String POST_URL = "https://android.googleapis.com/gcm/send";
    @Inject
    Logger log;
    @Inject
    private NotificationService notificationService;
    @Inject
    private ViewerService viewerService;
    private GcmContentModel gcmContentModel;

    public NotificationSender() {
        gcmContentModel = new GcmContentModel();
    }

    public NotificationSender(GcmContentModel gcmContentModel) {
        this.gcmContentModel = gcmContentModel;
    }

    public void send() {
        String payload = new Gson().toJson(this.gcmContentModel);
        log.info("payload " + payload);
        sendNotification(payload);
    }

    private void sendNotification(String data) {
        log.info("==================== Sending Push Notification ====================");
        log.info("data: " + data);
        OAuthRequest request = new OAuthRequest(Verb.POST, POST_URL);
        request.addHeader("Authorization", "key=" + API_KEY);
        request.addHeader("Content-Type", "application/json");
        request.addPayload(data);
        Response response = request.send();
        viewerService.checkResponse(response, data);
        log.info("response Code: " + response.getCode());
        log.info("response Body: " + response.getBody());
        log.info("===================================================================");
    }

    public void sendNotification(Notification notification) {
        try {

            String data = notification.getContent();
            sendNotification(data);
            notification.setStatus(PaymentStatusEnum.PROCESSED);

        } catch (RuntimeException e) {
            log.error("Error when send notification " + e);
            notification.setStatus(PaymentStatusEnum.ERROR);
            notification.setErrorDescription(e.getMessage());
        }

        notification.setSentDate(new Date());
        notificationService.update(notification);
    }
}




