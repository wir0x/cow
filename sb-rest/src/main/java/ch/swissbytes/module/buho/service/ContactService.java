package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dto.ContactDto;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.buho.service.notifications.NotificationService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ContactService {

    @Inject
    private NotificationService notificationService;


    public void createEmail(ContactDto contactDto) {
        Notification notification = Notification.createNew();
        notification.setToAddress(KeyAppConfiguration.getString(ConfigurationKey.SMTP_SENDER_ADDRESS_SUPPORT));
        notification.setSubject(contactDto.getTitle());
        notification.setFromName(contactDto.getFrom());
        notification.setContent(contactDto.getMessage() + " Send by: " + contactDto.getFrom());
        notificationService.add(notification);
    }
}
