package ch.swissbytes.module.buho.service;


import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.DateUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Date;

@Stateless
public class SmsControlService {

    @Inject
    private Logger log;

    @Inject
    private SmsControlDao smsControlDao;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private DeviceRepository deviceRepository;

    public SmsControl store(SmsControl detachedSmsControl) throws RuntimeException {
        log.info("storeCoordinates...");
        try {
            Optional<Device> deviceOptional = deviceRepository.getById(detachedSmsControl.getDeviceId());

            if (deviceOptional.isAbsent()) {
                throw new InvalidInputException("No se encontró el dispositivo con id: " + detachedSmsControl.getDeviceId());
            }

            Optional<SmsControl> smsControlOptional = smsControlDao.getById(detachedSmsControl.getId());

            if (smsControlOptional.isPresent()) {
                detachedSmsControl.setMonthYear(smsControlOptional.get().getMonthYear());
            }

            Date date = DateUtil.getFirstDayOfAMonth(detachedSmsControl.getMonthYear());
            detachedSmsControl.setMonthYear(date);
            detachedSmsControl = smsControlDao.merge(detachedSmsControl);
            return detachedSmsControl;
        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public void delete(Long smsControlId) throws RuntimeException {
        log.info("delete" + smsControlId);

        Optional<SmsControl> smsControlOptional = smsControlDao.getById(smsControlId);

        if (smsControlOptional.isAbsent()) {
            throw new InvalidInputException("no exist device by id: " + smsControlId);
        }

        smsControlDao.remove(smsControlOptional.get());
        log.info("Device removed: " + smsControlId);
    }

    public SmsControl createSmsControl(Subscription subscription) {
        log.info("createSmsControl " + subscription);
        Optional<SmsControl> smsControlOptional = smsControlDao.getCurrentByDeviceId(subscription.getDevice().getId());

        SmsControl smsControl;

        if (smsControlOptional.isAbsent()) {
            smsControl = SmsControl.createNew();
            smsControl.setDevice(subscription.getDevice());
            smsControl.setMonthYear(DateUtil.getFirstDayOfMonth());

            Optional<Subscription> subscriptionOptional = subscriptionRepository.getActiveByDeviceId(subscription.getDevice().getId());

            if (subscriptionOptional.isPresent()) {
                smsControl.setMaxSms(subscriptionOptional.get().getMaxSms());
            } else {
                smsControl.setMaxSms(KeyAppConfiguration.getInt(ConfigurationKey.SMS_MAX_LIMIT));
            }
        } else {
            smsControl = smsControlOptional.get();
        }

        smsControl.getMaxSms();
        return store(smsControl);

    }
}
