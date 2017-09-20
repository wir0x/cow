package ch.swissbytes.module.buho.service.notifications;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.exception.NotificationNotFoundException;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.app.notification.repository.NotificationRepository;
import ch.swissbytes.module.shared.exception.ValidationUtil;
import ch.swissbytes.module.shared.gcm.observer.PushNotificationObserver;
import ch.swissbytes.module.shared.notifications.mail.MailObserver;
import ch.swissbytes.module.shared.notifications.sms.configuration.SmsObserver;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.List;


@Stateless
public class NotificationService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private NotificationRepository notificationRepository;

    @Inject
    private MailObserver mailObserver;

    @Inject
    private SmsObserver smsObserver;

    @Inject
    private PushNotificationObserver pushNotificationObserver;

    public Notification add(Notification notification) {
        ValidationUtil.validateEntityFields(validator, notification);
        return notificationRepository.save(notification);
    }

    public void update(Notification notification) {
        findById(notification.getId());
        notificationRepository.update(notification);
    }

    private Notification findById(Long id) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        if (!notificationOptional.isPresent())
            throw new NotificationNotFoundException();

        return notificationOptional.get();
    }

    public void sendNotifications() {
        List<Notification> notifications = notificationRepository.findWithPendingStatus();

        for (Notification notification : notifications) {
            log.info("Notification Type: " + notification.getType());

            notification.setStatus(PaymentStatusEnum.PROCESSING);
            notificationRepository.update(notification);

            switch (notification.getType()) {
                case EMAIL:
                    mailObserver.send(notification);
                    break;
                case SMS:
                    smsObserver.send(notification);
                    break;
                case PUSH_NOTIFICATION:
                    pushNotificationObserver.send(notification);
                    break;
            }
        }
    }
}
