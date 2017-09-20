package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.AlarmBattery;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class AlarmBatteryDao extends Repository {

    public Optional<AlarmBattery> getByDeviceId(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(AlarmBattery.class, filter);
    }

    public Optional<AlarmBattery> getById(Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(AlarmBattery.class, id);
    }
}
