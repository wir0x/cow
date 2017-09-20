package ch.swissbytes.module.buho.app.device.repository;

import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.domain.enumerator.SellerEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
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
public class DeviceRepository extends Repository {

    public List<Device> findAll() {
        return findAll(Device.class);
    }

    public List<Device> findBySeller(SellerEnum sellerEnum) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("seller", sellerEnum);
        return findBy(Device.class, filter);
    }

    public List<Device> findAllEnabled() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", StatusEnum.ENABLED);
        return findBy(Device.class, filters);
    }

    public Optional<Device> findByTrackerId(String trackerId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("generatedId", trackerId);
        return getBy(Device.class, filter);
    }

    public Optional<Device> getByImei(String imei) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("imei", imei);
        return StringUtil.isEmpty(imei) ? Optional.absent() : getBy(Device.class, filters);
    }

    public List<Device> findByAccountId(Long id) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("account.id", id);
        return LongUtil.isEmpty(id) ? Collections.emptyList() : findBy(Device.class, filters);
    }

    public Optional<Device> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Device.class, id);
    }

    public Optional<DeviceType> getDeviceTypeById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(DeviceType.class, id);
    }

    public Optional<Device> getByGeneratedId(String generateId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("generatedId", generateId);
        return StringUtil.isEmpty(generateId) ? Optional.absent() : getBy(Device.class, filter);
    }

    public List<DeviceType> findAllDeviceType() {
        return findAll(DeviceType.class);
    }
}
