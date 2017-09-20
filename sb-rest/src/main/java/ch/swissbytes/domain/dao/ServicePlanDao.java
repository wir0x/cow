package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.ServicePlan;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ServicePlanDao extends Repository {

    public List<ServicePlan> findAll() {
        return findAll(ServicePlan.class);
    }

    public List<ServicePlan> findExcluding(Long excludedPlanId) {
        return findNotEquals(ServicePlan.class, "id", excludedPlanId);
    }

    public Optional<ServicePlan> findFreePlan() {
        Map<String, Object> filter = new HashMap<>();
        filter.put("price", 0);
        return getBy(ServicePlan.class, filter);
    }

    public Optional<ServicePlan> getById(Long servicePlanId) {
        return LongUtil.isEmpty(servicePlanId) ? Optional.absent() : getById(ServicePlan.class, servicePlanId);
    }
}
