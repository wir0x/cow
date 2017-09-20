package ch.swissbytes.module.buho.app.geofence.repository;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class GeoFenceRepository extends Repository {

    public List<GeoFence> findAll() {
        return findAll(GeoFence.class);
    }

    public List<GeoFence> findEnableFences(String weekDay) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("status", StatusEnum.ENABLED);
        filter.put(weekDay, true);
        return findBy(GeoFence.class, filter);
    }

    public List<GeoFence> findEnableFences(String weekDay, Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        filter.put("status", StatusEnum.ENABLED);
        filter.put(weekDay, true);
        return findBy(GeoFence.class, filter);
    }

    public List<GeoFence> findByDeviceId(final Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        return findBy(GeoFence.class, filters);
    }

    public Optional<GeoFence> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(GeoFence.class, id);
    }

    public List<GeoFence> findFencesByDeviceList(final List<Device> deviceList) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device", deviceList);
        return deviceList.isEmpty() ? Collections.emptyList() : findBy(GeoFence.class, filters);
    }
}
