package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.HistoryFreePlan;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.StringUtil;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class HistoryFreePlanDao extends Repository {

    public List<HistoryFreePlan> findAll() {
        return findAll(HistoryFreePlan.class);
    }

    public Optional<HistoryFreePlan> getByUniqueId(String uniqueId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("uniqueId", uniqueId);
        filter.put("status", StatusEnum.ENABLED);
        return StringUtil.isEmpty(uniqueId) ? Optional.absent() : getBy(HistoryFreePlan.class, filter);
    }
}
