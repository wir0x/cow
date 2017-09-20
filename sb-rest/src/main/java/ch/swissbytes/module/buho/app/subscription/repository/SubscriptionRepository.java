package ch.swissbytes.module.buho.app.subscription.repository;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.OrderBy;

import javax.ejb.Stateless;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class SubscriptionRepository extends Repository {

    public List<Subscription> findAll() {
        return findAll(Subscription.class);
    }

    public List<Subscription> findByDeviceId(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("shoppingCart", false);
        return LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public List<Subscription> findByUserId(Long userId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("user.id", userId);
        filters.put("shoppingCart", false);
        return LongUtil.isEmpty(userId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public List<Subscription> findByUserIdToDelete(Long userId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("user.id", userId);
        return LongUtil.isEmpty(userId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public List<Subscription> findAllByDeviceId(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        return LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public List<Subscription> findPlaceHoldersByDeviceId(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("status", StatusEnum.DISABLED);
        return LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public List<Subscription> findOnShoppingCarByDeviceId(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("shoppingCart", true);
        return LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(Subscription.class, filters);
    }

    public Optional<Subscription> getByDeviceIdAndShoppingCart(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("shoppingCart", true);
        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(Subscription.class, filters);
    }

    public Optional<Subscription> getByDeviceAndStatusPending(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("status", StatusEnum.PENDING);
        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(Subscription.class, filters);
    }

    public Optional<Subscription> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Subscription.class, id);
    }

    public Optional<Subscription> getActiveByDeviceId(Long deviceId) {
        Date currentDate = new Date();
        String query = "SELECT s FROM " +
                "Subscription s " +
                "WHERE s.device.id = :deviceId " +
                "AND s.endDate >= :currentDate " +
                "AND s.startDate <= :currentDate " +
                "AND s.shoppingCart = :shoppingCart " +
                "AND s.status = :status ";
        Map<String, Object> filters = new HashMap<>();
        filters.put("deviceId", deviceId);
        filters.put("currentDate", currentDate);
        filters.put("status", StatusEnum.ENABLED);
        filters.put("shoppingCart", false);

        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(query, filters);
    }

    public Optional<Subscription> getPendingSubscriptionByDeviceId(Long deviceId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("status", StatusEnum.PENDING);
        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(Subscription.class, filters);
    }

    public Subscription getActiveOrLastSubscriptionByDeviceId(Long deviceId) {
        Optional<Subscription> activeSubscription = getActiveByDeviceId(deviceId);

        if (activeSubscription.isPresent()) {
            return activeSubscription.get();
        }

        List<Subscription> subscriptionList = subscriptionListWithoutStatusDisabled(findByDeviceId(deviceId));

        if (subscriptionList.isEmpty()) {
            return new Subscription();
        }

        // return de last subscription.
        return subscriptionList.get(subscriptionList.size() - 1);

    }

    public Subscription getLastActiveSubscriptionByDeviceId(Long deviceId) {
        List<Subscription> subscriptionList = subscriptionListWithoutStatusDisabled(findByDeviceId(deviceId));

        if (subscriptionList.isEmpty()) {
            return new Subscription();
        }

        // return de last subscription.
        return subscriptionList.get(subscriptionList.size() - 1);

    }

    public List<Subscription> subscriptionListWithoutStatusDisabled(List<Subscription> subscriptionList) {
        return subscriptionList.stream()
                .filter(subscription -> subscription.getStatus() != StatusEnum.DISABLED)
                .collect(Collectors.toList());
    }

    public Subscription getLastSubscription(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        filter.put("shoppingCart", false);
        List<Subscription> subscriptionList = findBy(Subscription.class, filter);
        return subscriptionList.get(subscriptionList.size() - 1);
    }

    public Optional<Subscription> getLastActiveByDeviceId(Long deviceId) {
        Optional<Subscription> subscriptionOptional = getActiveByDeviceId(deviceId);
        List<Subscription> subscriptionList = findByDeviceId(deviceId);

        if (subscriptionList.size() == 1) {
            return new Optional<>(subscriptionList.get(0));
        }

        if (subscriptionOptional.isAbsent()) {
            return subscriptionOptional;
        }

        List<OrderBy> orderByList = new ArrayList<>();
        orderByList.add(new OrderBy("id", OrderBy.DESCENDING));

        Map<String, Object> filters = new HashMap<>();
        filters.put("device.id", deviceId);
        filters.put("shoppingCart", false);
        return LongUtil.isEmpty(deviceId) ? Optional.absent() : getBy(Subscription.class, filters, orderByList);
    }
}
