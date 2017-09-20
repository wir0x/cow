package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.HistoryFreePlanDao;
import ch.swissbytes.domain.entities.HistoryFreePlan;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

@Stateless
public class HistoryFreePlanService {

    @Inject
    private Logger log;

    @Inject
    private HistoryFreePlanDao historyFreePlanDao;


    public HistoryFreePlan store(final HistoryFreePlan detachedHistoryFreePlan) throws RuntimeException {
        try {
            log.info("store HistoryFreePlan: " + detachedHistoryFreePlan);
            Optional<HistoryFreePlan> historyFreePlanOpt = historyFreePlanDao.getByUniqueId(detachedHistoryFreePlan.getUniqueId());

            if (historyFreePlanOpt.isPresent()) {
                return historyFreePlanDao.merge(historyFreePlanOpt.get());
            } else {
                return historyFreePlanDao.save(detachedHistoryFreePlan);
            }

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public HistoryFreePlan storeFromSubscription(Subscription subscription) {
        HistoryFreePlan historyFreePlan = HistoryFreePlan.createNew();
        historyFreePlan.setUniqueId(subscription.getDevice().getImei());
        historyFreePlan.setDeviceName(subscription.getDevice().getName());
        historyFreePlan.setPlanId(subscription.getServicePlan().getId());
        historyFreePlan.setPlanName(subscription.getServicePlan().getName());
        return store(historyFreePlan);
    }

    public Boolean didHaveFreePlan(Device device) {
        String uniqueId = device.getImei();
        return historyFreePlanDao.getByUniqueId(uniqueId).isPresent();
    }

    public Boolean didNotHaveFreePlan(Device device) {
        return !didHaveFreePlan(device);
    }
}
