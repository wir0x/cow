package ch.swissbytes.module.buho.app.userdevice.repository;

import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.OrderBy;

import javax.ejb.Stateless;
import java.util.*;

@Stateless
public class UserDeviceRepository extends Repository {

    public List<UserDevice> findByUserId(Long userId) {
        List<OrderBy> orderByList = new ArrayList<>();
        orderByList.add(new OrderBy("device.name", OrderBy.ASCENDING));

        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return LongUtil.isEmpty(userId) || LongUtil.isEmpty(userId) ? Collections.emptyList() : findBy(UserDevice.class, filter, orderByList);
    }

    public List<UserDevice> findByUser(Long userId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return findBy(UserDevice.class, filter);
    }

    public List<UserDevice> findByDeviceId(Long deviceId) {
        List<OrderBy> orderByList = new ArrayList<>();
        orderByList.add(new OrderBy("device.name", OrderBy.ASCENDING));

        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        return findBy(UserDevice.class, filter, orderByList);
    }

    public List<UserDevice> findByDevice(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        return findBy(UserDevice.class, filter);
    }

    public List<UserDevice> findByUserList(List<User> userList) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user", userList);
        return findBy(UserDevice.class, filter);
    }

    public Optional<UserDevice> getByUserIdAndDeviceId(Long userId, Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        filter.put("device.id", deviceId);
        return LongUtil.isEmpty(userId) || LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(UserDevice.class, filter);
    }
}
