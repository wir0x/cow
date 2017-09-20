package ch.swissbytes.module.buho.app.subscription.service;

import ch.swissbytes.domain.dao.*;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.app.userrole.respository.UserRoleRepository;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.buho.service.HistoryFreePlanService;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.domain.dto.*;
import ch.swissbytes.domain.entities.*;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;

import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.notifications.mail.template.subscription.MailSenderSubscriptionAlert;
import ch.swissbytes.module.shared.notifications.push_notification.subscription.PushNotificationSubscriptionPlan;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.*;

@Stateless
public class SubscriptionService {

    @Inject
    private Logger log;
    @Inject
    private MailSenderSubscriptionAlert mailSenderSubscriptionAlert;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private UserService userService;
    @Inject
    private DeviceService deviceService;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ServicePlanDao servicePlanDao;
    @Inject
    private PushNotificationSubscriptionPlan pushNotificationSubscriptionPlan;
    @Inject
    private HistoryFreePlanService historyFreePlanService;
    @Inject
    private AccountService accountService;


    public SubscriptionStatusDto checkStatus(Device device) {

        if (historyFreePlanService.didNotHaveFreePlan(device)) {
            return activeFreePlan(device);
        }

        final Subscription subscription = subscriptionRepository.getLastActiveSubscriptionByDeviceId(device.getId());

        if (subscription.isNew()) {
            return SubscriptionStatusDto.forWithOutSubscription(device);
        }

        return SubscriptionStatusDto.forEnabledPlan(subscription);
    }

    public List<SubscriptionDto> subscriptionByAdminLogged(Identity identity) {
        if (identity.getIdentityDto().isBuhoAdmin()) {
            List<Subscription> subscriptions = subscriptionRepository.findAll();
            return SubscriptionDto.fromSubscriptionList(subscriptions);
        }
        if (identity.getIdentityDto().isUbidataAdmin()) {
            List<Subscription> subscriptions = subscriptionFilterByUbidata();
            return SubscriptionDto.fromSubscriptionList(subscriptions);
        }
        return Collections.emptyList();
    }

    private List<Subscription> subscriptionFilterByUbidata() {
        List<Subscription> subscriptions = new ArrayList<>();
        List<Account> accounts = accountService.ubidataAccounts();
        List<Device> devices = new ArrayList<>();
        for (Account account : accounts) {
             devices.addAll(deviceService.findByAccoundId(account.getId()));
        }
        for (Device device : devices) {
            subscriptions.addAll(subscriptionRepository.findByDeviceId(device.getId()));
        }
        return subscriptions;
    }

    public SubscriptionStatusDto activeFreePlan(Device device) {
        Subscription subscription = createFreeSubscription(device);
        historyFreePlanService.storeFromSubscription(subscription);
        return SubscriptionStatusDto.forFreePlan(subscription);
    }

    public Subscription update(Subscription subscription) {
        try {
            Subscription subscriptionResponse = subscriptionRepository.merge(subscription);
            log.info(String.format("Subscription updated!: %s", subscription));
            return subscriptionResponse;
        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public void deleteExceptFreeSubscriptionByDeviceID(Long deviceId) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByDeviceId(deviceId);
        Optional<ServicePlan> freeServicePlan = servicePlanDao.findFreePlan();

        if (freeServicePlan.isAbsent()) {
            log.error("no there are free plan with price 0");

        } else {
            subscriptions.stream().
                    filter(subscription -> !Objects.equals(subscription.getServicePlan().getId(), freeServicePlan.get().getId())).
                    forEach(subscription -> subscriptionRepository.remove(subscription));
        }
    }


    public List<Subscription> updateSubscriptionList(List<Subscription> subscriptionList, PaymentRequest paymentRequest) {
        List<Subscription> subscriptions = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            subscription.setPaymentRequest(paymentRequest);
            subscriptions.add(update(subscription));
        }
        return subscriptions;
    }

    public List<SubscriptionManagementDto> subscriptionListOfDevices(Long accountId) {
        List<SubscriptionManagementDto> subscriptionMgtDtoList = new ArrayList<>();

        // Device list of account.
        List<Device> deviceList = deviceRepository.findByAccountId(accountId);
        for (Device device : deviceList) {
            Optional<Subscription> subscriptionOptional = subscriptionRepository.getByDeviceIdAndShoppingCart(device.getId());
            Subscription subscription = subscriptionRepository.getLastActiveSubscriptionByDeviceId(device.getId());

            if (subscription.isNew()) {
                subscriptionMgtDtoList.add(SubscriptionManagementDto.withoutSubscription(device));
            } else {
                final boolean isOnShoppingCart = subscriptionOptional.isPresent();
                subscriptionMgtDtoList.add(SubscriptionManagementDto.fromSubscription(subscription, isOnShoppingCart));
            }
        }
        return subscriptionMgtDtoList;
    }

    public List<Subscription> updateSubscriptionList(List<SubscriptionPay> subscriptionPayList) {
        try {
            List<Subscription> subscriptionList = new ArrayList<>();
            for (SubscriptionPay subscriptionPay : subscriptionPayList) {
                Optional<Subscription> subscriptionOpt = subscriptionRepository.getById(subscriptionPay.getSubscriptionId());
                Subscription subscription = subscriptionOpt.get();
                subscription.setServicePlan(getServicePlan(subscriptionPay.getServicePlanId()));
                subscription.setStartDate(getStartDate(subscriptionPay.getServicePlanId(), subscriptionPay.getDeviceId()));
                subscription.setEndDate(getEndDate(subscriptionPay.getServicePlanId(), subscriptionPay.getDeviceId()));
                subscription.setStatus(StatusEnum.PENDING);
                subscription.setShoppingCart(true);
                // update subscription and add to list
                subscriptionList.add(update(subscription));
            }

            log.info(String.format("SubscriptionList updated!: %s", subscriptionList.size()));
            return subscriptionList;
        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    private ServicePlan getServicePlan(Long servicePlanId) {
        Optional<ServicePlan> servicePlanOpt = servicePlanDao.getById(servicePlanId);
        return servicePlanOpt.get();
    }

    private Date getStartDate(Long servicePlanId, Long deviceId) {
        return startAndEndDate(servicePlanId, deviceId).get("startDate");
    }

    private Date getEndDate(Long servicePlanId, Long deviceId) {
        return startAndEndDate(servicePlanId, deviceId).get("endDate");
    }

    public Map<String, Date> startAndEndDate(Long servicePlanId, Long deviceId) {
        Map<String, Date> startEndDate = new HashMap<>();
        Date endDate, startDate = new Date();
        Optional<ServicePlan> servicePlanOpt = servicePlanDao.getById(servicePlanId);
        Optional<Subscription> subscriptionOpt = subscriptionRepository.getLastActiveByDeviceId(deviceId);

        if (subscriptionOpt.isPresent())
            if (subscriptionOpt.get().getStatus().equals(StatusEnum.EXPIRED) || subscriptionOpt.get().getServicePlan() == null)
                startDate = new Date();
            else
                startDate = DateUtil.setDaysToDate(subscriptionOpt.get().getEndDate(), 1);

        endDate = DateUtil.setDaysToDate(startDate, servicePlanOpt.get().getDurationDays());
        startEndDate.put("startDate", startDate);
        startEndDate.put("endDate", endDate);
        return startEndDate;
    }

    public List<ShoppingCartDto> shoppingCartDtoListByAccountId(Long id) {
        List<Device> deviceList = deviceRepository.findByAccountId(id);
        List<ShoppingCartDto> shoppingCartDtoList = new ArrayList<>();
        log.info("deviceList: " + deviceList.size());

        for (Device device : deviceList) {
            Optional<Subscription> subscriptionOptional = subscriptionRepository.getByDeviceIdAndShoppingCart(device.getId());

            if (subscriptionOptional.isPresent()) {
                shoppingCartDtoList.add(ShoppingCartDto.from(subscriptionOptional.get()));
            }
        }
        return shoppingCartDtoList;
    }


    public Subscription storeNewSubscription(User user, Device device, Subscription subscription) {
        log.info(String.format("storeNewSubscription => %s, %s, %s", user.getName(), device.getName(), subscription.getStatus()));
        try {
            validateSubscription(subscription);

            if (subscription.isNew()) {
                subscription.setUser(user);
                subscription.setDevice(device);
                subscription = subscriptionRepository.save(subscription);
            } else {
                subscription = subscriptionRepository.merge(subscription);
            }
            log.info(String.format("Subscription created: %s", subscription));
            return subscription;

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public Subscription storeNewSubscription(Subscription detachedSubscription) throws RuntimeException {
        try {
            validateSubscription(detachedSubscription);

            if (detachedSubscription.isNew()) {
                log.info("is new subscription");
                detachedSubscription = subscriptionRepository.save(detachedSubscription);
            } else {
                log.info("update subscription ");
                detachedSubscription = subscriptionRepository.merge(detachedSubscription);
            }
            return detachedSubscription;

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public void deletePlaceHoldersSubscription(List<Subscription> subscriptionList) {
        log.info("deletePlaceHoldersSubscription-> " + subscriptionList.size());
        for (Subscription subscription : subscriptionList) {
            List<Subscription> subscriptions = subscriptionRepository.findPlaceHoldersByDeviceId(subscription.getDevice().getId());
            subscriptionRepository.delete(subscriptions);
        }
    }

    public void delete(Long subscriptionId) throws EJBException {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.getById(subscriptionId);

        if (subscriptionOpt.isPresent()) {
            subscriptionRepository.remove(subscriptionOpt.get());
        }
    }

    public void removeSubscription(Long subscriptionId) throws EJBException {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.getById(subscriptionId);
        validateRemoveSubscription(subscriptionOpt);

        Subscription subscription = subscriptionOpt.get();
        Optional<UserDevice> userDeviceOpt = userDeviceRepository.getByUserIdAndDeviceId(subscription.getUser().getId(), subscription.getDevice().getId());

        subscriptionRepository.remove(subscription);
        userDeviceRepository.remove(userDeviceOpt.get());
        userRoleService.removeUserRole(subscription.getUser().getId());
        userRepository.remove(subscription.getUser());
        deviceRepository.remove(subscription.getDevice());
        log.info("subscription removed! " + subscriptionId);
    }

    public void validateRemoveSubscription(Optional<Subscription> subscriptionOpt) throws EJBException {
        if (subscriptionOpt.isAbsent())
            throw new InvalidInputException("not exist this subscription ");
        Subscription subscription = subscriptionOpt.get();
//        if (subscription.getStatus() != StatusEnum.DISABLED)
//            throw new InvalidInputException("You can't disabled this subscription");
    }

    public void validateSubscription(Subscription subscription) throws InvalidInputException {
        log.info("ValidateSubscription: " + subscription);
        Optional<Subscription> subscriptionOptional = subscriptionRepository.getById(subscription.getId());

        if (subscription.getEndDate().getTime() < subscription.getStartDate().getTime())
            throw new InvalidInputException("La fecha final debe ser mayor a la fecha inicio");

        if (subscriptionOptional.isPresent() && !subscriptionOptional.get().getId().equals(subscription.getId()))
            throw new InvalidInputException("Esta suscription ya existe");
    }

    public ServicePlan getFreePlan() {
        Long freePlanId = KeyAppConfiguration.getLong(ConfigurationKey.FREE_PLAN_ID);
        return getPlan(freePlanId);
    }

    public ServicePlan getPlan(Long planId) {
        Optional<ServicePlan> servicePlanOptional = servicePlanDao.getById(planId);

        if (servicePlanOptional.isAbsent()) {
            log.error("[ERROR]: service plan null ");
        }
        return servicePlanOptional.get();
    }

    public Subscription createFreeSubscription(Device device) {
        ServicePlan freePlan = getFreePlan();
        Subscription subscription = Subscription.createNew();
        subscription.setDevice(device);
        subscription.setServicePlan(freePlan);
        subscription.setStartDate(new Date());
        subscription.setEndDate(DateUtil.setDaysToCurrentDate(freePlan.getDurationDays()));
        subscription.setMaxSms(0);
        subscription.setStatus(StatusEnum.ENABLED);
        subscription.setShoppingCart(false);
        subscription = subscriptionRepository.merge(subscription);
        return subscription;
    }

    public Subscription addOnShoppingCart(Long deviceId, Long planId) {
        Optional<Device> deviceOptional = deviceRepository.getById(deviceId);
        Optional<ServicePlan> servicePlanOptional = servicePlanDao.getById(planId);
        Long freePlanId = KeyAppConfiguration.getLong(ConfigurationKey.FREE_PLAN_ID);
        Subscription subscriptionOnCart = Subscription.createNew();

        // Add on shopping cart only: device present, service plan present and plans of pay (not free plan).
        if (deviceOptional.isPresent() && servicePlanOptional.isPresent() && !Objects.equals(servicePlanOptional.get().getId(), freePlanId)) {

            Device device = deviceOptional.get();
            ServicePlan servicePlan = servicePlanOptional.get();

            Subscription subscription = subscriptionRepository.getLastActiveSubscriptionByDeviceId(deviceId);

            if (subscription.isNew() || subscription.getStatus() == StatusEnum.EXPIRED) {

                subscriptionOnCart = newSubscription(device, servicePlan);

            } else if (subscription.getStatus() == StatusEnum.ENABLED) {
                subscriptionOnCart = fromEnableSubscription(device, servicePlan, subscription);
            }

            // Set true flag shopping cart | set status disable when stay on shopping cart | store new subscription.
            subscriptionOnCart.setShoppingCart(true);
            subscriptionOnCart.setStatus(StatusEnum.DISABLED);
            subscriptionOnCart = subscriptionRepository.merge(subscriptionOnCart);

        } else {
            log.warn(String.format("device: %s or planId: %s not exist!", deviceId, planId));
        }

        return subscriptionOnCart;
    }

    private Subscription newSubscription(Device device, ServicePlan servicePlan) {
        Subscription newSubscription = Subscription.createNew();
        newSubscription.setStartDate(new Date());
        newSubscription.setEndDate(DateUtil.setDaysToDate(newSubscription.getStartDate(), servicePlan.getDurationDays()));
        newSubscription.setServicePlan(servicePlan);
        newSubscription.setDevice(device);
        newSubscription.setUserPay(false);
        newSubscription.setStatus(StatusEnum.ENABLED);
        return newSubscription;
    }

    private Subscription fromEnableSubscription(Device device, ServicePlan servicePlan, Subscription enabledSubscription) {
        Subscription newSubscription = Subscription.createNew();
        newSubscription.setStartDate(DateUtil.setDaysToDate(enabledSubscription.getEndDate(), 1));
        newSubscription.setEndDate(DateUtil.setDaysToDate(newSubscription.getStartDate(), servicePlan.getDurationDays()));
        newSubscription.setServicePlan(servicePlan);
        newSubscription.setDevice(device);
        newSubscription.setUserPay(false);
        newSubscription.setStatus(StatusEnum.ENABLED);
        return newSubscription;
    }

    public void createSubscriptionNotifications() {
        final long start = System.currentTimeMillis();
        List<Device> deviceList = deviceRepository.findAllEnabled();
        deviceList.forEach(this::sendNotification);
        final long end = System.currentTimeMillis();
        log.info("Create notification subscription: " + (end - start) + "ms.");
    }

    private void sendNotification(Device device) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.getLastActiveByDeviceId(device.getId());

        if (subscriptionOptional.isPresent()) {
            checkFirstSubscriptionNotification(subscriptionOptional.get());
            checkSecondSubscriptionNotification(subscriptionOptional.get());
            checkIfSubscriptionExpired(subscriptionOptional.get());
        }
    }

    private void checkIfSubscriptionExpired(Subscription subscription) {
        if (subscription.getEndDate().before(new Date())) {
            subscription.setStatus(StatusEnum.EXPIRED);
            update(subscription);
        }
    }

    private void checkFirstSubscriptionNotification(Subscription subscription) {
        final int fsr = KeyAppConfiguration.getInt(ConfigurationKey.REMAINING_DAYS_SUBSCRIPTION_1);
        final Date addDays1 = DateUtil.setDaysToCurrentDate(fsr);

        if (datesEquals(addDays1, subscription.getEndDate())) {
            mailSenderSubscriptionAlert.createEmailNotification(subscription.getDevice(), fsr);
            pushNotificationSubscriptionPlan.createPushNotification(subscription.getDevice(), fsr);
        }
    }

    private void checkSecondSubscriptionNotification(Subscription subscription) {
        final int ssr = KeyAppConfiguration.getInt(ConfigurationKey.REMAINING_DAYS_SUBSCRIPTION_2);
        final Date addDays2 = DateUtil.setDaysToCurrentDate(ssr);

        if (datesEquals(addDays2, subscription.getEndDate())) {
            mailSenderSubscriptionAlert.createEmailNotification(subscription.getDevice(), ssr);
            pushNotificationSubscriptionPlan.createPushNotification(subscription.getDevice(), ssr);
        }
    }

    public Subscription getByDeviceId(Long id) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.getActiveByDeviceId(id);

        if (subscriptionOpt.isPresent())
            return subscriptionOpt.get();

        List<Subscription> subscriptions = subscriptionRepository.findByDeviceId(id);
        if (subscriptions.isEmpty())
            return Subscription.createNew();

        return getLast(subscriptions);
    }

    private Subscription getLast(List<Subscription> subscriptions) {
        return subscriptions.get(subscriptions.size() - 1);
    }

    public SubscriptionPayTMDto validatePayTigoMoney(SubscriptionPayTMDto subscriptionPayTMDto) throws InvalidInputException {
        List<SubscriptionPay> subscriptionPayList = subscriptionPayTMDto.getSubscriptionPayList();
        SubscriptionPayTMDto responsePaymentDto = subscriptionPayTMDto;
        String businessName = subscriptionPayTMDto.getBusinessName();

        if (StringUtil.isEmpty(businessName)) subscriptionPayTMDto.setBusinessName("S/N");

        if (LongUtil.isEmpty(subscriptionPayTMDto.getNit())) subscriptionPayTMDto.setNit(0L);

        if (LongUtil.isInteger(subscriptionPayTMDto.getTigoPhoneNumber()))
            responsePaymentDto.setTigoPhoneNumber(subscriptionPayTMDto.getTigoPhoneNumber());
        else throw new InvalidInputException("Escriba un número de teléfono válido");

        if (subscriptionPayTMDto.getTigoPhoneNumber().length() > 8)
            throw new InvalidInputException("El número no debe tener mas de 8 dígitos");

        Integer line = Integer.parseInt(subscriptionPayTMDto.getTigoPhoneNumber());

        if (!validatePrefixTigoNumbers(line))
            throw new InvalidInputException("El numero Tigo es requerido para el pago");

        if (LongUtil.isEmpty(line)) throw new InvalidInputException("El numero Tigo es requerido para el pago");

        validateSubscriptionPay(subscriptionPayList);

        return subscriptionPayTMDto;
    }

    private void validateSubscriptionPay(List<SubscriptionPay> subscriptionPayList) throws InvalidInputException {
        for (SubscriptionPay subscriptionPay : subscriptionPayList) {
            if (deviceRepository.getById(subscriptionPay.getDeviceId()).isAbsent())
                throw new InvalidInputException(String.format("El dipositivo %s no existe! ", subscriptionPay.getDeviceId()));

            if (subscriptionRepository.getById(subscriptionPay.getSubscriptionId()).isAbsent())
                throw new InvalidInputException(String.format("La Suscripcion %s  no existe! ", subscriptionPay.getSubscriptionId()));

            if (servicePlanDao.getById(subscriptionPay.getServicePlanId()).isAbsent())
                throw new InvalidInputException(String.format("El plan %s no existe!", subscriptionPay.getServicePlanId()));
        }
    }

    private boolean validatePrefixTigoNumbers(Integer tigoPhoneNumber) throws InvalidInputException {
        String prefixPhoneNumberTigo = KeyAppConfiguration.getString(ConfigurationKey.PREFIX_PHONE_NUMBERS_TIGO);
        String tigoPhoneNumberStr = tigoPhoneNumber.toString();
        boolean flag = false;

        for (String prefix : prefixPhoneNumberTigo.split(";"))
            if (tigoPhoneNumberStr.substring(0, 2).equals(prefix)) flag = true;
        return flag;
    }

    private boolean datesEquals(Date date1, Date date2) {
        return DateUtil.setZeroHour(date1).equals(DateUtil.setZeroHour(date2));
    }

    public void removeUserOfSubscription(Long userId) {
        List<Subscription> subscriptionList = subscriptionRepository.findByUserIdToDelete(userId);
        subscriptionRepository.delete(subscriptionList);
    }

    public List<ServicePlan> servicePlanList() {
        Long freePlanId = KeyAppConfiguration.getLong(ConfigurationKey.FREE_PLAN_ID);
        return servicePlanDao.findExcluding(freePlanId);
    }

    public Subscription firstSubscription(Device device, ServicePlan servicePlan) {
        Subscription subscription = Subscription.createNew();
        subscription.setDevice(device);
        subscription.setServicePlan(servicePlan);
        subscription.setStartDate(new Date());
        subscription.setEndDate(DateUtil.setDaysToCurrentDate(servicePlan.getDurationDays()));
        subscription.setShoppingCart(false);
        subscription.setStatus(StatusEnum.DISABLED);
        subscription = subscriptionRepository.merge(subscription);
        return subscription;
    }

    private Subscription createSubscription(Date lastDate, ServicePlan servicePlan, Device device) {
        Subscription subscription = Subscription.createNew();
        subscription.setDevice(device);
        subscription.setServicePlan(servicePlan);
        subscription.setStartDate(DateUtil.setDaysToDate(lastDate, 1));
        subscription.setEndDate(DateUtil.setDaysToDate(subscription.getStartDate(), servicePlan.getDurationDays()));
        subscription.setShoppingCart(false);
        subscription.setStatus(StatusEnum.DISABLED);
        subscription = subscriptionRepository.merge(subscription);
        return subscription;
    }

    public Subscription createSubscription(Device device, ServicePlan servicePlan) {
        List<Subscription> subscriptions = subscriptionRepository.findByDeviceId(device.getId());

        if (subscriptions.isEmpty()) {
            createFreeSubscription(device);
            return firstSubscription(device, servicePlan);
        }

        Subscription subscription = subscriptionRepository.getLastSubscription(device.getId());
        return createSubscription(subscription.getEndDate(), servicePlan, device);
    }
}
