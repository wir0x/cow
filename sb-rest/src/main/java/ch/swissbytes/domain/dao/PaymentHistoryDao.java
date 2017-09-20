package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.PaymentHistory;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class PaymentHistoryDao extends Repository {

    public Optional<PaymentHistory> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(PaymentHistory.class, id);
    }

    public List<PaymentHistory> findByAccountIdAndStatusEnabled(Long accountId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("account.id", accountId);
        filter.put("status", StatusEnum.ENABLED);
        return LongUtil.isEmpty(accountId) ? Collections.emptyList() : findBy(PaymentHistory.class, filter);
    }

    public List<PaymentHistory> findAll() {
        return findAll(PaymentHistory.class);
    }

    public List<PaymentHistory> findByAccountId(Long accountId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("account.id", accountId);
        return LongUtil.isEmpty(accountId) ? Collections.emptyList() : findBy(PaymentHistory.class, filter);
    }

    public List<PaymentHistory> findByAccountIdAndDeviceId(Long accountId, Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("account.id", accountId);
        filter.put("deviceId", deviceId);
        return LongUtil.isEmpty(accountId) || LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(PaymentHistory.class, filter);
    }

    public List<PaymentHistory> findByAccountIdAndPaymentStatus(Long accountId, PaymentStatusEnum paymentStatus) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("account.id", accountId);
        filter.put("statusPayment", paymentStatus);
        return LongUtil.isEmpty(accountId) ? Collections.emptyList() : findBy(PaymentHistory.class, filter);
    }

    public List<PaymentHistory> findByAccountIdDeviceIdPaymentStatus(Long accountId, Long deviceId, PaymentStatusEnum statusPayment) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("account.id", accountId);
        filter.put("deviceId", deviceId);
        filter.put("statusPayment", statusPayment);
        return findBy(PaymentHistory.class, filter);
    }

    public List<PaymentHistory> findByPaymentStatus(PaymentStatusEnum paymentStatusEnum) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("statusPayment", paymentStatusEnum);
        return findBy(PaymentHistory.class, filter);
    }
}
