package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.PaymentRequestDao;
import ch.swissbytes.domain.dao.ServicePlanDao;
import ch.swissbytes.domain.dto.*;
import ch.swissbytes.domain.entities.PaymentRequest;
import ch.swissbytes.domain.entities.PaymentRequestItem;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.ewallet.service.EWalletService;
import ch.swissbytes.ewallet.tigomoney.action.TigoMoneyServiceImpl;
import ch.swissbytes.ewallet.tigomoney.dto.TMConnectionDto;
import ch.swissbytes.ewallet.tigomoney.dto.TMPaymentItemDto;
import ch.swissbytes.ewallet.tigomoney.dto.TMPaymentRequestDto;
import ch.swissbytes.ewallet.tigomoney.dto.TMPaymentResponseDto;
import ch.swissbytes.ewallet.util.TMEntityUtil;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.*;
import java.util.concurrent.Future;

@Stateless
public class PaymentRequestService {

    @Inject
    private Logger log;

    @Inject
    private PaymentRequestDao paymentRequestDao;

    @Inject
    private ConfigurationRepository configurationRepository;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private AccountRepository accountDao;

    @Inject
    private ServicePlanDao servicePlanDao;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private GcmControlService gcmControlService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    public PaymentRequest storePaymentRequest(final PaymentRequest detachedRequest) throws RuntimeException {
        try {
            if (detachedRequest.isNew()) {
                detachedRequest.setCreationDate(new Date());
                return paymentRequestDao.save(detachedRequest);
            } else {
                return paymentRequestDao.merge(detachedRequest);
            }
        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    /**
     * Prepare tigo money payment for app tracker.
     *
     * @param buySubscriptionDto
     * @param subscription
     * @return
     */
    public PaymentRequestDto prepareTigoMoneyPayment(BuySubscriptionDto buySubscriptionDto, Subscription subscription) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        //required fields
        paymentRequestDto.setUserId(null);
        paymentRequestDto.setDeviceId(subscription.getDevice().getId());
        paymentRequestDto.setServicePlanId(subscription.getServicePlan().getId());
        paymentRequestDto.setStatus(PaymentStatusEnum.PENDING);

        paymentRequestDto.setAmount(subscription.getServicePlan().getPrice());
        paymentRequestDto.setCorrectUrl("http://www.buho.bo");
        paymentRequestDto.setErrorUrl("http://www.buho.bo");
        paymentRequestDto.setLine(buySubscriptionDto.getTigoPhoneNumber());

        //other fields
        paymentRequestDto.setDocumentNumber(buySubscriptionDto.getTigoIdCardNumber());
        paymentRequestDto.setMessage("Suscripcion a servicio de rastreo satelital - " + subscription.getServicePlan().getName());
        paymentRequestDto.setName(subscription.getDevice().getName());
        paymentRequestDto.setConfirmation(" por el servicio de BUHO GPS tracker");
        paymentRequestDto.setNotification("Buho: Suscripcion");

        //invoice fields
        paymentRequestDto.setBusinessName(buySubscriptionDto.getBusinessName());
        paymentRequestDto.setNit(String.valueOf(buySubscriptionDto.getNit()));

        //payment items
        List<TMPaymentItemDto> paymentItems = new ArrayList<>();
        TMPaymentItemDto paymentItem = new TMPaymentItemDto();
        paymentItem.setConcept(subscription.getServicePlan().getDescription());
        paymentItem.setQuantity(1);
        paymentItem.setSerial("1");
        paymentItem.setUnitPrice(subscription.getServicePlan().getPrice());
        paymentItem.setTotalPrice(subscription.getServicePlan().getPrice());
        paymentItems.add(paymentItem);
        paymentRequestDto.setItems(paymentItems);
        return paymentRequestDto;
    }

    private Double getPriceShoppingCart(List<Subscription> subscriptionList) {
        Double price = 0.0;
        for (Subscription subscription : subscriptionList) {
            price += subscription.getServicePlan().getPrice();
        }
        log.info("getPriceShoppingCart=> " + price.doubleValue());
        return price.doubleValue();
    }

    public PaymentRequestDto prepareTIGOMoneyPayment(List<Subscription> subscriptionList, SubscriptionPayTMDto subscriptionPayTMDto, Account account) {
        PaymentRequestDto paymentDto = new PaymentRequestDto();
        //required fields

        paymentDto.setUserId(account.getId());

        paymentDto.setStatus(PaymentStatusEnum.PENDING);
        paymentDto.setAmount(getPriceShoppingCart(subscriptionList));
        paymentDto.setCorrectUrl("http://www.test.buho.bo");
        paymentDto.setErrorUrl("http://www.test.buho.bo");
        paymentDto.setLine(Integer.parseInt(subscriptionPayTMDto.getTigoPhoneNumber()));

        //other fields
        paymentDto.setDocumentNumber(subscriptionPayTMDto.getTigoIdCardNumber());
        paymentDto.setMessage("Compra de " + subscriptionList.size() + " suscripciones.");
        paymentDto.setName(account.getName());
        paymentDto.setConfirmation(" por el servicio de BUHO GPS tracker");
        paymentDto.setNotification("Buho: Suscripcion");

        //invoice fields
        paymentDto.setBusinessName(subscriptionPayTMDto.getBusinessName());
        paymentDto.setNit(String.valueOf(subscriptionPayTMDto.getNit()));

        //payment items
        List<TMPaymentItemDto> paymentItems = new ArrayList<>();

        for (Subscription subscription : subscriptionList) {
            TMPaymentItemDto paymentItem = new TMPaymentItemDto();
            paymentItem.setConcept(subscription.getServicePlan().getDescription());
            paymentItem.setQuantity(1);
            paymentItem.setSerial("1");
            paymentItem.setUnitPrice(subscription.getServicePlan().getPrice());
            paymentItem.setTotalPrice(subscription.getServicePlan().getPrice());
            paymentItems.add(paymentItem);
        }
        paymentDto.setItems(paymentItems);

        log.info("paymentDto=> " + paymentDto);
        return paymentDto;
    }

    public PaymentRequest storePaymentRequest(PaymentRequestDto paymentRequestDto) {
        PaymentRequest paymentRequest = storePaymentRequest(paymentRequestDto.toPaymentRequestEntity());

        for (TMPaymentItemDto itemDto : paymentRequestDto.getItems()) {
            PaymentRequestItem item = new PaymentRequestItem();
            item.setSerial(itemDto.getSerial());
            item.setConcept(itemDto.getConcept());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setTotalPrice(itemDto.getTotalPrice());
            item.setPaymentRequest(paymentRequest);
            storePaymentRequestItem(item);
        }
        return paymentRequest;
    }

    private PaymentRequestItem storePaymentRequestItem(PaymentRequestItem detachedItem) {
        try {
            if (detachedItem.isNew()) {
                return paymentRequestDao.save(detachedItem);
            } else {
                return paymentRequestDao.merge(detachedItem);
            }
        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    private List<PaymentRequestItem> findItemsByPaymentRequestId(Long paymentRequestId) {
        Map<String, Object> params = new HashMap<>();
        params.put("paymentRequest.id", paymentRequestId);
        return paymentRequestDao.findBy(PaymentRequestItem.class, params);
    }

    /***
     * Star process Tigo Money from WEB
     *
     * @param payment
     * @param subscriptionList
     * @param orderId
     * @param identity
     * @return
     */

    @Asynchronous
    public Future<String> startTigoMoneyPayment(PaymentRequest payment, List<Subscription> subscriptionList, String orderId, Identity identity) {
        log.info(String.format("startTigoMoneyPayment--> payment: %s SubscriptionPay:  %s", payment, subscriptionList.size()));
        Optional<User> userEntityOptional = userRepository.getById(identity.getIdentityDto().getId());
        List<PaymentRequestItem> paymentItems = findItemsByPaymentRequestId(payment.getId());

        // TIGO Money parameter configuration (id key, encryptionKey, url)
        TMConnectionDto tmConnectionDto = TMConnectionDto.createNew();
        tmConnectionDto.setIdKey(configurationRepository.getTigoMoneyIdKey());
        tmConnectionDto.setEncryptionKey(configurationRepository.getTigoMoneyEncryptKey());
        tmConnectionDto.setUrl(configurationRepository.getTigoMoneyUrlWebservice());

        // Process
        EWalletService eWalletService = new TigoMoneyServiceImpl(tmConnectionDto);
        TMPaymentRequestDto tmPaymentRequest = PaymentRequestDto.newFromEntity(payment, paymentItems, orderId);
        TMPaymentResponseDto tmPaymentResponse = eWalletService.requestPayment(tmPaymentRequest);

        try {
            switch (tmPaymentResponse.getCode()) {
                case 0:
                    log.info("success payment!");
                    List<Subscription> subscriptions = enableSubscriptionList(subscriptionList);
                    payment.setStatus(PaymentStatusEnum.PROCESSED);
                    payment.setStatusDetail("");
                    sendSuccessPushNotification(subscriptions);
                    break;
                default:
                    log.warn("failed payment! " + tmPaymentRequest.getMessage());
                    revertShoppingCart(subscriptionList);
                    payment.setStatus(PaymentStatusEnum.ERROR);
                    payment.setStatusDetail(tmPaymentResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("error!" + e.getMessage());
            payment.setStatus(PaymentStatusEnum.ERROR);
            payment.setStatusDetail("Exception: " + e.getMessage());
        } finally {
            storePaymentRequest(payment);
            paymentHistoryService.storeFromWeb(subscriptionList, payment, userEntityOptional.get());
            return new AsyncResult<>("OK");
        }
    }


    /**
     * Start tigo money payment from app (tracker or viewer)
     *
     * @param payment
     * @param subscription
     * @param orderId
     * @param identityDto
     * @param gcmToken
     * @return
     */
    @Asynchronous
    public Future<String> startTigoMoneyPayment(PaymentRequest payment, Subscription subscription, String orderId, IdentityDto identityDto, String gcmToken) {
        // Payment items.
        List<PaymentRequestItem> paymentItems = findItemsByPaymentRequestId(payment.getId());

        // Create push notification to app tracker.
        PaymentPushNotificationDto pushDto = PaymentPushNotificationDto.createNew();
        pushDto.setTitle("Buho Tracking Service");
        pushDto.setToken(gcmToken);

        // Prepare tigo money parameter (id key, encryptionKey, url).
        TMConnectionDto tmConnectionDto = TMConnectionDto.createNew();
        tmConnectionDto.setIdKey(configurationRepository.getTigoMoneyIdKey());
        tmConnectionDto.setEncryptionKey(configurationRepository.getTigoMoneyEncryptKey());
        tmConnectionDto.setUrl(configurationRepository.getTigoMoneyUrlWebservice());

        // Start process API EWalletService.
        EWalletService eWalletService = new TigoMoneyServiceImpl(tmConnectionDto);
        TMPaymentRequestDto tmPaymentRequest = PaymentRequestDto.newFromEntity(payment, paymentItems, orderId);
        TMPaymentResponseDto tmPaymentResponse = eWalletService.requestPayment(tmPaymentRequest);

        try {
            switch (tmPaymentResponse.getCode()) {
                case 0:
                    log.info("TM payment success. ");
                    // Enable subscription because payment was success.
                    subscription = enableSubscription(subscription, payment);

                    // Change status to SUCCESS payment because payment was success.
                    payment.setStatus(PaymentStatusEnum.PROCESSED);
                    payment.setStatusDetail(tmPaymentResponse.getDescription());

                    // Build push notification message.
                    pushDto.setMessage("Enhorabuena! Su suscripcion ha sido habilitada exitosamente.");
                    pushDto.setInnerData(SubscriptionDto.fromSubscriptionEntity(subscription));

                    // Save transaction on history payment.
                    paymentHistoryService.storeFromPaymentApp(subscription, identityDto);
                    break;

                default:
                    log.warn("TM payment failed. " + tmPaymentResponse.getDescription());

                    //set payment to subscription.
                    subscription.setPaymentRequest(payment);

                    // Change status to ERROR payment because payment was failed.
                    payment.setStatus(PaymentStatusEnum.ERROR);
                    payment.setStatusDetail(tmPaymentResponse.getDescription());

                    // Save transaction on history payment.
                    paymentHistoryService.storeFromPaymentApp(subscription, identityDto);

                    // Delete subscription because payment failed.
                    deleteSubscription(subscription);

                    // Build push notification message to app tracker.
                    pushDto.setMessage(tmPaymentResponse.getDescription());
                    pushDto.setErrorType(tmPaymentResponse.getCode());
            }

        } catch (Exception e) {

            log.error("[PAYMENT ERROR]: " + e.getMessage());

            // Set status ERROR to payment.
            payment.setStatus(PaymentStatusEnum.ERROR);
            payment.setStatusDetail("Exception: " + e.getMessage() + " | TM:" + tmPaymentResponse.getMessage());

            // Send push notification to app tracker (payment failed)
            pushDto.setMessage("Su transaccion no se pudo procesar, intente nuevamente por favor.");
            pushDto.setErrorType(TMEntityUtil.DEFAULT_ERROR_CODE);

        } finally {

            // Store payment request.
            storePaymentRequest(payment);

            // Send push notification to app tracker.
            gcmControlService.sendNotification(pushDto);

            return new AsyncResult<>("OK");
        }
    }

    private void sendSuccessPushNotification(List<Subscription> subscriptionList) {
        for (Subscription subscription : subscriptionList) {
            if (StringUtil.isNotEmpty(subscription.getDevice().getImei()) && StringUtil.isNotEmpty(subscription.getDevice().getGcmToken())) {
                PaymentPushNotificationDto pushDto = PaymentPushNotificationDto.createNew();
                pushDto.setTitle("Buho Tracking Service");
                pushDto.setToken(subscription.getDevice().getGcmToken());
                pushDto.setMessage("Enhorabuena! Su suscripcion ha sido habilitada exitosamente.");
                Account account = subscription.getDevice().getAccount();
                pushDto.setInnerData(SubscriptionDto.fromSubscriptionEntity(subscription, subscription.getDevice(), account));
                gcmControlService.sendNotification(pushDto);
            } else
                log.info("device: " + subscription.getDevice() + " not has imei or gcm token");
        }
    }

    private List<Subscription> enableSubscriptionList(List<Subscription> subscriptions) {
        log.info("ENABLED subscriptions list " + subscriptions.size());
        List<Subscription> subscriptionList = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            subscription.setStatus(StatusEnum.ENABLED);
            subscription.setUserPay(false);
            subscription.setShoppingCart(false);
            subscription = subscriptionService.update(subscription);
            subscriptionList.add(subscription);
        }
        // disabled place holder subscription
        log.info("DELETE Subscription list ");
        subscriptionService.deletePlaceHoldersSubscription(subscriptionList);
        return subscriptionList;
    }

    private Subscription enableSubscription(Subscription subscription, PaymentRequest paymentRequest) {
        subscription.setStatus(StatusEnum.ENABLED);
        subscription.setPaymentRequest(paymentRequest);
        subscription = subscriptionService.update(subscription);
        return subscription;
    }


    private void revertShoppingCart(List<Subscription> subscriptionList) {
        log.info("revertShoppingCart-->  " + subscriptionList.size());
        for (Subscription subscription : subscriptionList) {
            subscription.setShoppingCart(true);
            subscription.setStatus(StatusEnum.DISABLED);
            subscriptionService.update(subscription);
        }
    }

    private void deleteSubscription(Subscription subscription) {
        subscriptionService.delete(subscription.getId());
    }
}
