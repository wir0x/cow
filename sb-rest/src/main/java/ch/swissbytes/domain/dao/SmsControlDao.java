package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.DateUtil;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class SmsControlDao extends Repository {

    public List<SmsControl> findAll() {
        return findAll(SmsControl.class);
    }

    public Optional<SmsControl> getById(final Long id) {
        return id == null || id == 0 ? Optional.absent() : getById(SmsControl.class, id);
    }

    public Optional<SmsControl> getCurrentByDeviceId(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        Date date = DateUtil.getDateFormatted(DateUtil.getFirstDayOfMonth());

        filter.put("monthYear", date);
        filter.put("device.id", deviceId);
        return getBy(SmsControl.class, filter);
    }

    public List<SmsControl> findByDeviceId(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        return findBy(SmsControl.class, filter);
    }
}
