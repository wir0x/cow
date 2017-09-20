package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ViewerDao extends Repository {

    public Optional<Viewer> getById(Long id) {
        return getById(Viewer.class, id);
    }

    public Optional<Viewer> getByUniqueId(String uniqueId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("imei", uniqueId);
        return StringUtil.isEmpty(uniqueId) ? Optional.absent() : getBy(Viewer.class, filter);
    }

    public Optional<Viewer> getByGcmToken(String gcmToken) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("gcmToken", gcmToken);
        return StringUtil.isEmpty(gcmToken) ? Optional.absent() : getBy(Viewer.class, filter);
    }

    public List<Viewer> findByUserId(Long id) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", id);
        filter.put("status", StatusEnum.ENABLED);
        return LongUtil.isEmpty(id) ? Collections.<Viewer>emptyList() : findBy(Viewer.class, filter);
    }

    public List<Viewer> findByUserIdToDelete(Long userId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return LongUtil.isEmpty(userId) ? Collections.<Viewer>emptyList() : findBy(Viewer.class, filter);
    }
}
