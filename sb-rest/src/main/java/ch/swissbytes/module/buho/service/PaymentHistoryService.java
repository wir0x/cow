package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.PaymentHistoryDao;
import ch.swissbytes.domain.dto.HistoryPaymentDto;
import ch.swissbytes.domain.entities.PaymentHistory;
import ch.swissbytes.domain.entities.PaymentRequest;
import ch.swissbytes.domain.enumerator.ApplicationTypeEnum;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Stateless
public class PaymentHistoryService {

    @Inject
    private Logger log;

    @Inject
    private PaymentHistoryDao paymentHistoryDao;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    public List<PaymentHistory> storeFromWeb(List<Subscription> subscriptionList, PaymentRequest paymentRequest, User user) throws RuntimeException {
        try {
            log.info("storeFromWeb -> " + subscriptionList.size());
            List<PaymentHistory> paymentHistoryList = new ArrayList<>();

            for (Subscription subscription : subscriptionList) {
                subscription.setPaymentRequest(paymentRequest);
                paymentHistoryList.add(paymentHistoryDao.merge(fromEntities(subscription, user, ApplicationTypeEnum.WEB.getLabel())));
            }

            return paymentHistoryList;

        } catch (PersistenceException pEx) {
            log.error("[ERROR] PersistenceException: " + pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public PaymentHistory storeFromPaymentApp(Subscription subscription, IdentityDto identityDto) throws RuntimeException {
        try {
            PaymentHistory paymentHistory;

            if (identityDto == null) {
                paymentHistory = fromEntities(subscription, ApplicationTypeEnum.TRACKER.getLabel());

            } else {
                paymentHistory = fromEntities(subscription, ApplicationTypeEnum.VIEWER.getLabel(), identityDto);
            }

            return paymentHistoryDao.merge(paymentHistory);


        } catch (PersistenceException pEx) {
            log.error("[ERROR] storeFromPaymentApp : " + pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public void updateFromDto(HistoryPaymentDto[] historyPaymentDtos) {
        for (HistoryPaymentDto historyPaymentDto : historyPaymentDtos) {
            Optional<PaymentHistory> paymentHistoryOpt = paymentHistoryDao.getById(historyPaymentDto.getId());
            if (paymentHistoryOpt.isPresent()) {
                paymentHistoryOpt.get().setStatus(StatusEnum.DISABLED);
                paymentHistoryDao.merge(paymentHistoryOpt.get());
            }
        }
    }

    public PaymentHistory fromEntities(Subscription subscription, User user, String payFrom) {
        PaymentHistory paymentHistory = PaymentHistory.createNew();
        paymentHistory.setId(subscription.getPaymentRequest().getId());
        paymentHistory.setLine(subscription.getPaymentRequest().getLine());
        paymentHistory.setAmount(subscription.getServicePlan().getPrice());
        paymentHistory.setPlan(subscription.getServicePlan().getName());
        paymentHistory.setDeviceId(subscription.getDevice().getId());
        paymentHistory.setDeviceName(subscription.getDevice().getName());
        paymentHistory.setUserId(user.getId());
        paymentHistory.setUserName(user.getName());
        paymentHistory.setCreationDate(new Date());
        paymentHistory.setStatusPayment(subscription.getPaymentRequest().getStatus());
        paymentHistory.setPayFrom(payFrom);
        paymentHistory.setAccount(subscription.getDevice().getAccount() != null ? subscription.getDevice().getAccount() : null);
        return paymentHistory;
    }

    public PaymentHistory fromEntities(Subscription subscription, String payFrom) {
        PaymentHistory paymentHistory = PaymentHistory.createNew();
        paymentHistory.setId(subscription.getPaymentRequest().getId());
        paymentHistory.setLine(subscription.getPaymentRequest().getLine());
        paymentHistory.setAmount(subscription.getServicePlan().getPrice());
        paymentHistory.setPlan(subscription.getServicePlan().getName());
        paymentHistory.setDeviceId(subscription.getDevice().getId());
        paymentHistory.setDeviceName(subscription.getDevice().getName());
        paymentHistory.setUserId(null);
        paymentHistory.setUserName(null);
        paymentHistory.setCreationDate(new Date());
        paymentHistory.setStatusPayment(subscription.getPaymentRequest().getStatus());
        paymentHistory.setPayFrom(payFrom);
        paymentHistory.setAccount(subscription.getDevice().getAccount() != null ? subscription.getDevice().getAccount() : null);
        return paymentHistory;
    }

    public PaymentHistory fromEntities(Subscription subscription, String payFrom, IdentityDto identityDto) {
        PaymentHistory paymentHistory = PaymentHistory.createNew();
        paymentHistory.setId(subscription.getPaymentRequest().getId());
        paymentHistory.setLine(subscription.getPaymentRequest().getLine());
        paymentHistory.setAmount(subscription.getServicePlan().getPrice());
        paymentHistory.setPlan(subscription.getServicePlan().getName());
        paymentHistory.setDeviceId(subscription.getDevice().getId());
        paymentHistory.setDeviceName(subscription.getDevice().getName());
        paymentHistory.setUserId(identityDto.getId());
        paymentHistory.setUserName(identityDto.getName());
        paymentHistory.setCreationDate(new Date());
        paymentHistory.setStatusPayment(subscription.getPaymentRequest().getStatus());
        paymentHistory.setPayFrom(payFrom);
        paymentHistory.setAccount(new Account(identityDto.getAccountId()));
        return paymentHistory;
    }


    public List<PaymentHistory> findTransactions(Long accountId, Long deviceId, String statusPayment) {
        if ((accountId == 0) && (deviceId == 0) && (statusPayment.equals("ALL"))) {
            log.info("Find all transactions");
            return paymentHistoryDao.findAll();
        }

        if ((deviceId == 0) && (statusPayment.equals("ALL"))) {
            log.info("Find by account");
            return paymentHistoryDao.findByAccountId(accountId);
        }

        if ((deviceId != 0) && (statusPayment.equals("ALL"))) {
            log.info("Find by account and status");
            return paymentHistoryDao.findByAccountIdAndDeviceId(accountId, deviceId);
        }

        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.getLabel(statusPayment);

        if ((accountId == 0) && (deviceId == 0) && (!statusPayment.equals("ALL"))) {
            log.info("Find by status payment");
            return paymentHistoryDao.findByPaymentStatus(paymentStatusEnum);
        }

        if ((deviceId == 0) && (!statusPayment.equals("ALL"))) {
            log.info("Find by account and status payment");
            return paymentHistoryDao.findByAccountIdAndPaymentStatus(accountId, paymentStatusEnum);
        }

        log.info("Find by account device and status");
        return paymentHistoryDao.findByAccountIdDeviceIdPaymentStatus(accountId, deviceId, paymentStatusEnum);
    }
}
