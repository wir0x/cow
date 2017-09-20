package ch.swissbytes.module.buho.rest.endpoint;


import ch.swissbytes.domain.dao.*;
import ch.swissbytes.domain.dto.*;
import ch.swissbytes.domain.entities.*;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.domain.enumerator.SubscriptionErrorEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.role.repository.RoleRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.buho.service.HistoryFreePlanService;
import ch.swissbytes.module.buho.service.PaymentRequestService;
import ch.swissbytes.module.buho.service.SmsControlService;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import net.minidev.json.JSONObject;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.*;
import java.util.stream.Collectors;

@Path("subscription")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class SubscriptionRestEndPoint {

    @Inject
    private Logger log;
    @Inject
    private Identity identity;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private HistoryFreePlanDao historyFreePlanDao;
    @Inject
    private HistoryFreePlanService historyFreePlanService;
    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private ServicePlanDao servicePlanDao;
    @Inject
    private ConfigurationRepository configurationRepository;
    @Inject
    private DeviceService deviceService;
    @Inject
    private AccountRepository accountDao;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private PaymentRequestDao paymentRequestDao;
    @Inject
    private PaymentHistoryDao paymentHistoryDao;
    @Inject
    private PaymentRequestService paymentRequestService;
    @Inject
    private SmsControlService smsControlService;
    @Inject
    private SmsControlDao smsControlDao;
    @Inject
    private UserService userService;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private PositionRepository positionRepository;
    @Inject
    private AppConfiguration labels;
    @Inject
    private ViewerDao viewerDao;


    @GET
    @Path("smartphone/status/{imei}")
    public Response checkStatus(@PathParam("imei") String imei) {

        log.info("*************** CHECK STATUS SMARTPHONE SUBSCRIPTION ***************");
        log.info("imei: " + imei);

        final Optional<Device> deviceOptional = deviceRepository.getByImei(imei);

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).entity("device not found").build();
        }

        final Device device = deviceOptional.get();
        SubscriptionStatusDto subscriptionStatusDto = subscriptionService.checkStatus(device);

        log.info("response: " + subscriptionStatusDto);
        return Response.ok(subscriptionStatusDto).build();
    }

    @GET
    @Path("/list")
    @LoggedIn
    public Response findAllSubscription() {
        try {
            log.info("*************** ALL SUBSCRIPTIONS ***************");
            List<SubscriptionDto> dto = subscriptionService.subscriptionByAdminLogged(identity);
            return Response.ok().entity(dto).build();

        } catch (RuntimeException e) {

            log.error("RuntimeException: " + e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {

            log.error("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }

    @GET
    @Path("/list/service-plan")
    @LoggedIn
    public Response findAllServicePlan() {
        try {

            log.info("**************** SERVICE PLAN LIST FOR APP **************");

            return Response.ok().entity(servicePlanDao.findAll()).build();

        } catch (Exception e) {
            log.error("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }

    @GET
    @Path("service-plan")
    public Response servicePlanList() {
        try {

            log.info("**************** SERVICE PLAN LIST FROM WEB ****************");

            Long freePlanId = KeyAppConfiguration.getLong(ConfigurationKey.FREE_PLAN_ID);
            List<ServicePlan> servicePlanList = servicePlanDao.findExcluding(freePlanId);

            return Response.ok(servicePlanList).build();

        } catch (Exception ex) {
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }

    @GET
    @Path("/list/{deviceId}")
    @LoggedIn
    public Response getSubscription(@PathParam("deviceId") Long deviceId) {
        try {
            log.info("************* SUBSCRIPTION OF DEVICE ************");
            log.info("deviceId: " + deviceId);

            Subscription subscription = subscriptionRepository.getLastActiveSubscriptionByDeviceId(deviceId);

            if (subscription.isNew()) {
                return Response.ok().entity(SubscriptionDto.createNew()).build();
            }

            SubscriptionDto subscriptionDto = SubscriptionDto.fromEntity(subscription);

            log.info("response -> " + subscriptionDto);
            return Response.ok().entity(subscriptionDto).build();

        } catch (RuntimeException e) {
            log.error("RuntimeException: " + e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }

    @GET
    @Path("{deviceId}")
    public Response lastSubscription(@PathParam("deviceId") Long deviceId) {

        log.info("**************** GET ACTIVE OR LAST SUBSCRIPTION ****************  ");
        log.info("deviceId: " + deviceId);

        Optional<Device> deviceOptional = deviceRepository.getById(deviceId);

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).entity("device not found").build();
        }

        Device device = deviceOptional.get();
        Subscription subscription = subscriptionRepository.getLastActiveSubscriptionByDeviceId(deviceId);

        if (subscription.isNew()) {
            return Response.ok(SubscriptionResponseDto.withoutSubscription(device)).build();
        }

        return Response.ok(SubscriptionResponseDto.fromEntity(subscription)).build();

    }

    @GET
    @Path("/smart/list/{imei}")
    @LoggedIn
    public Response getSmartSubscription(@PathParam("imei") String imei) {
        try {
            log.info("************* Check status subscription by imei  ****************");
            log.info("imei: " + imei);
            Long accountId = identity.getIdentityDto().getId();
            Device device = deviceService.linkingDevice(accountId, imei);
            Optional<Device> deviceOpt = deviceRepository.getByImei(imei);
            log.info("device: " + device);
            log.info("device Exist? " + deviceOpt.isPresent());

            if (deviceOpt.isAbsent()) {
                log.info("create new device");
                device.setImei(imei);
                device.setDeviceType(new DeviceType(KeyAppConfiguration.getLong(ConfigurationKey.SMARTPHONE_TYPE_ID)));
                device = deviceService.storeDevice(device);
            } else
                device = deviceOpt.get();

            log.info("device1: " + device);

            if (StringUtil.isEmpty(device.getName()))
                return subscriptionResponse(device, SubscriptionErrorEnum.NEW_DEVICE);

            if (deviceOpt.isPresent()) {
                Optional<Subscription> subscriptionOpt = subscriptionRepository.getByDeviceAndStatusPending(device.getId());
                log.info("Pending subscription ? " + subscriptionOpt.isPresent());
                if (subscriptionOpt.isPresent()) {
                    if (subscriptionOpt.get().getPaymentRequest() != null) {
                        Optional<PaymentRequest> paymentRequestOptional = paymentRequestDao.getById(subscriptionOpt.get().getPaymentRequest().getId());
                        log.info("paymentRequest? " + paymentRequestOptional.isPresent());
                        if (paymentRequestOptional.isPresent()) {
                            switch (paymentRequestOptional.get().getStatus()) {
                                case ERROR:
                                    return subscriptionResponse(device, SubscriptionErrorEnum.TM_ERROR);
                                case PENDING:
                                    return subscriptionResponse(device, SubscriptionErrorEnum.TM_PENDING);
                                case PROCESSED:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            Optional<UserDevice> userDeviceOpt = userDeviceRepository.getByUserIdAndDeviceId(accountId, device.getId());
            List<UserDevice> userDeviceList = userDeviceRepository.findByDeviceId(device.getId());
            log.info("userDevice: " + userDeviceList);
            log.info("exists User and Device: " + userDeviceOpt.isPresent());

            if (!userDeviceList.isEmpty() && userDeviceOpt.isAbsent())
                return subscriptionResponse(device, SubscriptionErrorEnum.DEVICE_IN_USE);

            if (userDeviceOpt.isAbsent())
                return subscriptionResponse(device, SubscriptionErrorEnum.NEW_DEVICE);

            List<Subscription> subscriptionList = subscriptionRepository.findByDeviceId(device.getId());
            Optional<Subscription> subscriptionOpt = subscriptionRepository.getActiveByDeviceId(device.getId());
            log.info("subscriptionLIst: " + subscriptionList.size());
            log.info("has subscription active: " + subscriptionOpt.isPresent());

            if (subscriptionList.isEmpty() && subscriptionOpt.isAbsent()) {
                return subscriptionResponse(device, SubscriptionErrorEnum.EXPIRED);
            }

            if (subscriptionOpt.isAbsent())
                return subscriptionResponse(device, SubscriptionErrorEnum.NO_SUBSCRIPTION);

            if (subscriptionOpt.get().getUserPay())
                return subscriptionResponse(device, SubscriptionErrorEnum.USER_PAY);

            if (!subscriptionOpt.get().getUserPay() && subscriptionOpt.get().getStatus() == StatusEnum.DISABLED)
                return subscriptionResponse(device, SubscriptionErrorEnum.PAY_PENDING);

            return subscriptionResponse(device, SubscriptionErrorEnum.ACTIVE);

        } catch (RuntimeException e) {
            log.error("RuntimeException: " + e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/info/{imei}")
    @LoggedIn
    public Response getSubscriptionInformationByDeviceI(@PathParam("imei") String imei) {
        try {

            log.info("**************** Get Info Subscription by imei ****************** ");
            log.info("imei: " + imei);

            return responseInfoSubscription(imei);

        } catch (RuntimeException e) {

            log.error("RuntimeException: " + e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {

            log.error("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }


    @GET
    @Path("/management/list")
    @LoggedIn
    public Response findAllSubscriptionByAccount() {
        try {

            log.info("********** SUBSCRIPTION LIST OF DEVICES BY ACCOUNT  ***********");

            Long accountId = identity.getIdentityDto().getAccountId();
            List<SubscriptionManagementDto> subscriptionMgtDtoList = subscriptionService.subscriptionListOfDevices(accountId);
            return Response.ok(subscriptionMgtDtoList).build();

        } catch (RuntimeException e) {
            log.error("RuntimeException: " + e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.info("Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }


    @GET
    @Path("/plans")
    public Response getServicePlans() {
        try {

            log.info("*************** SERVICE PLAN LIST *************");

            List<ServicePlan> servicePlans = subscriptionService.servicePlanList();
            log.info("response-> " + servicePlans.size());

            return Response.ok(servicePlans).build();

        } catch (RuntimeException e) {
            log.error("RuntimeException: " + e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("/status/pay/tm/{imei}")
    public Response checkStatusPaymentForSmartphone(@PathParam("imei") String imei) {

        log.info("*************** CHECK IF THERE ARE PAYMENT IN PROGRESS *************");
        log.info("imei: " + imei);

        Optional<Device> deviceOptional = deviceRepository.getByImei(imei);

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).entity("device not found").build();
        }

        Device device = deviceOptional.get();
        List<Subscription> subscriptionList = subscriptionRepository.findByDeviceId(device.getId());

        if (subscriptionList.isEmpty()) {
            return Response.status(Status.NOT_FOUND).entity("device without subscriptions").build();
        }

        Subscription subscription = subscriptionList.get(subscriptionList.size() - 1);
        // If the last subscription status is DISABLED there are payment in progress.
        Boolean status = subscription.getStatus() == StatusEnum.DISABLED && !subscription.getShoppingCart();
        return Response.ok(status).build();
    }

    @POST
    @Path("/buy/tm")
    public Response buySubscription(BuySubscriptionDto buySubscriptionDto) {
        try {
            log.info("**************** BUY AND PAY SUBSCRIPTION WITH TIGO MONEY FROM APP ****************");
            log.info("input: " + buySubscriptionDto);

            IdentityDto identityDto = identity.getIdentityDto();

            Optional<Device> deviceOptional = deviceRepository.getById(buySubscriptionDto.getDeviceId());
            Optional<Viewer> viewerOptional = viewerDao.getByUniqueId(buySubscriptionDto.getImei());

            if (deviceOptional.isAbsent()) {
                log.info("device not found");
                return Response.status(Status.NOT_FOUND).entity("device not found").build();
            }

            Device device = deviceOptional.get();
            Long servicePlanId = buySubscriptionDto.getServicePlanId();

            // What APP to send push notification: tracker or viewer.
            String gcmToken = device.getGcmToken();
            if (identityDto != null && viewerOptional.isPresent()) {
                gcmToken = viewerOptional.get().getGcmToken();
            }

            // CREATE SUBSCRIPTION:
            ServicePlan servicePlan = subscriptionService.getPlan(servicePlanId);
            Subscription subscription = subscriptionService.createSubscription(device, servicePlan);

            // PAY SUBSCRIPTION: tigo money payment process.

            // Prepare Tigo money payment dto.
            PaymentRequestDto paymentRequestDto = paymentRequestService.prepareTigoMoneyPayment(buySubscriptionDto, subscription);

            // Prepare payment request.
            PaymentRequest paymentRequest = paymentRequestService.storePaymentRequest(paymentRequestDto);

            // Generate order id for payment.
            String orderId = generateOrderId(subscription, paymentRequest);

            // Start process tigo money.
            paymentRequestService.startTigoMoneyPayment(paymentRequest, subscription, orderId, identityDto, gcmToken);

            return Response.ok().entity(paymentRequest.getId()).build();

        } catch (EJBException ejbEx) {

            InvalidInputException iIEx = new InvalidInputException(ejbEx);
            log.error("[ERROR - pay Tigo Money] Invalid Input" + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(iIEx.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR - Pay Tigo money] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/validate/tigo-money")
    @LoggedIn
    public Response validateTigoMoney(SubscriptionPayTMDto subscriptionPayTMDto) {
        try {

            log.info("*************** VALIDATE AND PAY WITH TIGO MONEY FROM WEB *************");
            log.info("input: " + subscriptionPayTMDto);

            Optional<Account> account = accountDao.getById(identity.getIdentityDto().getAccountId());

            // Validate parameters Tigo money.
            subscriptionService.validatePayTigoMoney(subscriptionPayTMDto);

            // Update subscription for pay.
            List<Subscription> subscriptionList = subscriptionService.updateSubscriptionList(subscriptionPayTMDto.getSubscriptionPayList());
            PaymentRequestDto paymentRequestDto = paymentRequestService.prepareTIGOMoneyPayment(subscriptionList, subscriptionPayTMDto, account.get());

            // store payment request.
            PaymentRequest paymentRequest = paymentRequestService.storePaymentRequest(paymentRequestDto);
            // update subscriptions with payment request **/
            subscriptionList = subscriptionService.updateSubscriptionList(subscriptionList, paymentRequest);

            // Get order id generated.
            String orderId = getOrderIdWeb(paymentRequest.getId(), subscriptionList.size());
            log.info("orderId: " + orderId);

            // Start process tigo money.
            paymentRequestService.startTigoMoneyPayment(paymentRequest, subscriptionList, orderId, identity);
            return Response.ok().entity(paymentRequest.getId()).build();


        } catch (InvalidInputException e) {

            InvalidInputException invalidInputException = new InvalidInputException(e);
            log.error("[ERROR - ] InvalidInputException: " + e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/pay/status/{paymentId}")
    @LoggedIn
    public Response checkStatusPayment(@PathParam("paymentId") Long paymentId) {
        try {
            log.info("************* Check status payment by payment Id ************");
            log.info("paymentId: " + paymentId);
            Optional<PaymentRequest> paymentRequestOpt = paymentRequestDao.getById(paymentId);
            if (paymentRequestOpt.isAbsent())
                Response.ok().build();

            PaymentRequest paymentRequest = paymentRequestOpt.get();

            JSONObject statusPayment = new JSONObject();
            statusPayment.put("status", paymentRequest.getStatus().getLabel());
            statusPayment.put("statusDetailed", paymentRequest.getStatusDetail());
            return Response.ok().entity(statusPayment).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/history/pay")
    @LoggedIn
    public Response historyPayments() {
        try {
            log.info("*********** History Payments by Account ************");
            Long accountId = identity.getIdentityDto().getAccountId();
            List<HistoryPaymentDto> historyPaymentDtoList = new ArrayList<>();
            List<PaymentHistory> paymentHistoryList = paymentHistoryDao.findByAccountIdAndStatusEnabled(accountId);

            historyPaymentDtoList.addAll(paymentHistoryList
                    .stream()
                    .map(HistoryPaymentDto::fromPaymentHistory)
                    .collect(Collectors.toList()));

            Collections.sort(historyPaymentDtoList, (s1, s2) -> s2.getCreationDate().compareTo(s1.getCreationDate()));

            return Response.ok().entity(historyPaymentDtoList).build();

        } catch (RuntimeException e) {
            log.error("RuntimeException error!!! " + e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Exception error!!! " + e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/pay/check-status")
    @LoggedIn
    public Response checkPendingPayment() {
        try {
            log.info("********** Check PENDING Payment **********");
            Long accountId = identity.getIdentityDto().getAccountId();
            List<Device> deviceList = deviceRepository.findByAccountId(accountId);
            JSONObject statusPay = new JSONObject();
            statusPay.put("id", null);
            statusPay.put("isInProcess", false);

            for (Device device : deviceList) {
                Optional<Subscription> subscriptionOpt = subscriptionRepository.getPendingSubscriptionByDeviceId(device.getId());
                if (subscriptionOpt.isPresent()) {
                    Subscription subscription = subscriptionOpt.get();
                    if (subscription.getPaymentRequest() != null) {
                        Long paymentId = subscription.getPaymentRequest().getId();
                        Optional<PaymentRequest> paymentOpt = paymentRequestDao.getById(paymentId);
                        log.info("paymentOpt -> " + paymentOpt.get().getId());
                        if (paymentOpt.get().getStatus() == PaymentStatusEnum.PENDING) {
                            statusPay.put("id", paymentOpt.get().getId());
                            statusPay.put("isInProcess", true);
                        }
                    }
                }
            }

            return Response.ok().entity(statusPay).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx, invalidInputException);
            return Response.ok(Boolean.FALSE).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @POST
    @Path("/smart/create/tracker")
    @LoggedIn
    public Response createSmartphone(SmartphoneTrackerDto smartphoneTrackerDto) {
        try {
            log.info("************ create smartphone subscription ***********");
            log.info("input: " + smartphoneTrackerDto);
            Optional<Device> optSmartphone = deviceRepository.getByImei(smartphoneTrackerDto.getImei());

            if (optSmartphone.isAbsent())
                throw new Exception("Unknown gcm token and imei");

            IdentityDto identityDto = identity.getIdentityDto();
            Device device = optSmartphone.get();
            deviceService.storeNewSmartphone(getCurrentAccount()
                    , identityDto.getId()
                    , smartphoneTrackerDto.getDeviceName()
                    , device);

            Subscription subscription = Subscription.createNew();
            subscription.setStatus(StatusEnum.DISABLED);
            subscription.setDevice(device);
            subscription.setUser(new User(identity.getIdentityDto().getId()));
            subscription.setUserPay(true);
            subscriptionService.storeNewSubscription(subscription);
            return Response.ok(Boolean.TRUE).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx, invalidInputException);
            return Response.ok(Boolean.FALSE).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("create-subscription")
    @LoggedIn
    public Response createSubscription(NewSubscriptionDto newSubscriptionDto) {
        try {
            log.info("************* Create Subscription from backend **************");
            log.info("input: " + newSubscriptionDto);
            Long accountId = identity.getIdentityDto().getAccountId();
            // Create user from new subscription
            User user = userService.storeUser(newSubscriptionDto.userFromDto(accountId));

            // Create user Role
            UserRole userRole = newSubscriptionDto.userRoleFromDto();
            userRole.setUser(user);
            userRoleService.storeUserRole(userRole);

            // Create template device and set phone number to device
            Device device = newSubscriptionDto.deviceFromDto(accountId);
            device.setPhoneNumber(newSubscriptionDto.getUsername());
            device = deviceService.storeDevice(device);

            // Store user device with new user and device
            userDeviceService.storeUserDevice(new UserDevice(user, device));

            // Set and store device new device created to user admin of account.
            userDeviceService.setDeviceToUserAdmin(device);

            // Create and store template subscription to new new created
            Subscription subscription = subscriptionService.storeNewSubscription(user, device, newSubscriptionDto.subscriptionFromDto());

            return responseCreateSubscription(user, subscription, device);

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update/user-pay")
    @LoggedIn
    public Response updateCreationSubscription(NewSubscriptionDto newSubscriptionDto) {
        try {
            log.info("************** Update Subscription created **************");
            log.info("input: " + newSubscriptionDto);
            if (LongUtil.isEmpty(newSubscriptionDto.getId()) || newSubscriptionDto.getUserPay() == null)
                return Response.noContent().build();

            Optional<Subscription> subscriptionOpt = subscriptionRepository.getById(newSubscriptionDto.getId());
            if (subscriptionOpt.isAbsent()) return Response.status(Response.Status.NOT_FOUND).build();

            Subscription subscription = subscriptionOpt.get();
            subscription.setUserPay(newSubscriptionDto.getUserPay());
            subscriptionService.update(subscriptionOpt.get());
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }

    }

    @POST
    @Path("/create")
    @LoggedIn
    public Response createSubscription(SubscriptionDto subscriptionDto) {
        try {
            log.info("************ Create Subscription from backend*************");
            log.info("input: " + subscriptionDto);
            Subscription subscription = SubscriptionDto.fromSubscriptionDto(subscriptionDto);
            Long subscriptionId = subscriptionService.storeNewSubscription(subscription).getId();
            smsControlService.createSmsControl(subscription);
            return Response.ok().entity(subscriptionId).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("shopping-cart/{id}")
    @LoggedIn
    public Response deleteSubscriptionOfShoppingCart(@PathParam("id") Long id) {
        try {
            log.info("************* Delete subscription from shopping cart ***************");
            log.info("id: " + id);

            Optional<Subscription> subscriptionOpt = subscriptionRepository.getById(id);

            if (subscriptionOpt.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            subscriptionService.delete(id);

            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("shopping-cart")
    @LoggedIn
    public Response addToShoppingCart(List<SubscriptionReserveDto> subscriptionReserveDtoList) {
        try {
            log.info("************* ADD TO SHOPPING CART *************");
            log.info("subscriptionReserveDtoList: " + subscriptionReserveDtoList.size());

            if (subscriptionReserveDtoList.isEmpty()) {
                return Response.status(Status.BAD_REQUEST).build();
            }

            List<Long> subscriptionIdList = new ArrayList<>();

            for (SubscriptionReserveDto subscriptionReserveDto : subscriptionReserveDtoList) {

                Long deviceId = subscriptionReserveDto.getDeviceId();
                Long planId = subscriptionReserveDto.getPlanId();

                subscriptionIdList.add(subscriptionService.addOnShoppingCart(deviceId, planId).getId());
            }

            return Response.ok(subscriptionIdList).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();

        }
    }

    @GET
    @Path("shopping-cart")
    @LoggedIn
    public Response findShoppingCarts() {
        try {

            log.info("******************* SHOPPING CART LIST *******************");

            Long accountId = identity.getIdentityDto().getAccountId();
            List<ShoppingCartDto> shoppingCartDtoList = subscriptionService.shoppingCartDtoListByAccountId(accountId);

            if (shoppingCartDtoList.isEmpty()) {
                return Response.ok(ShoppingCartDto.createNew()).build();
            }

            return Response.ok().entity(shoppingCartDtoList).build();

        } catch (Exception e) {
            log.error("[ERROR]: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("shopping-cart/items")
    @LoggedIn
    public Response findItemsShoppingCart() {
        try {

            log.info("************** DEVICE LIST ON SHOPPING CART ****************");

            List<Device> deviceList = deviceRepository.findByAccountId(identity.getIdentityDto().getAccountId());
            int itemsOnShoppingCart = 0;

            for (Device device : deviceList) {
                List<Subscription> subscriptionList = subscriptionRepository.findOnShoppingCarByDeviceId(device.getId());
                itemsOnShoppingCart += subscriptionList.size();
            }

            return responseItemsShoppingCart(deviceList, itemsOnShoppingCart);

        } catch (RuntimeException e) {

            log.error("RuntimeException: " + e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {

            log.error("Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();

        }
    }

    @PUT
    @Path("/update")
    @LoggedIn
    public Response updateSubscription(SubscriptionDto subscriptionDto) {
        try {
            log.info("************** Update Subscription **************");
            log.info("input: " + subscriptionDto);
            Subscription subscription = SubscriptionDto.fromSubscriptionDto(subscriptionDto);
            subscriptionService.storeNewSubscription(subscription);
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @LoggedIn
    public Response deleteSubscription(@PathParam("id") Long id) {
        try {
            log.info("************ DELETE SUBSCRIPTION ***********");
            log.info("id: " + id);

            subscriptionService.delete(id);
            return Response.ok(Response.Status.OK).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("management/disabled/{subscriptionId}")
    @LoggedIn
    public Response removeSubscription(@PathParam("subscriptionId") Long subscriptionId) {
        try {
            log.info("************ Remove Subscription *************");
            log.info("subscriptionId: " + subscriptionId);
            subscriptionService.removeSubscription(subscriptionId);
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private List<SubscriptionDto> fromSubscriptionList(List<Subscription> subscriptionList) {
        List<SubscriptionDto> subscriptionDtoList = new ArrayList<>();

        for (Subscription subscription : subscriptionList) {
            SubscriptionDto subscriptionDto = SubscriptionDto.fromSubscriptionEntity(subscription
                    , subscription.getDevice()
                    , subscription.getDevice().getAccount());
            subscriptionDtoList.add(subscriptionDto);
        }
        return subscriptionDtoList;
    }

    private Response responseCreateSubscription(User user, Subscription subscription, Device device) {
        JSONObject responseData = new JSONObject();
        responseData.put("deviceId", device.getId());
        responseData.put("password", user.getGeneratedPassword());
        responseData.put("subscriptionId", subscription.getId());
        responseData.put("icon", device.getIcon());
        responseData.put("color", device.getColor());
        log.info("response -> " + responseData);
        return Response.ok().entity(responseData).build();
    }

    private Response subscriptionResponse(Device device, SubscriptionErrorEnum subscriptionErrorEnum) {
        log.info("subscription response: " + subscriptionErrorEnum);
        log.info("device: " + device);
        Map<String, Object> response = new HashMap<>();
        response.put("subscriptionStatesId", subscriptionErrorEnum.getId());
        response.put("description", subscriptionErrorEnum.getDescription());
        response.put("accountName", device.getAccount() != null ? device.getAccount().getName() : "");
        response.put("deviceName", device.getName());
        response.put("icon", device.getIcon());
        response.put("color", device.getColor());
        log.info("response -> " + response);
        return Response.ok().entity(response).build();
    }

    private Response responseError(Integer subscriptionErrorId) {
        JSONObject response = new JSONObject();
        response.put("accountName", "");
        response.put("subscriptionInit", "");
        response.put("subscriptionEnd", "");
        response.put("lastPositionSent", "");
        response.put("subscriptionStatus", subscriptionErrorId);
        log.info("[ERROR] response -> " + response);
        return Response.ok(response).build();
    }

    private Response responseInfoSubscription(String imei) {
        Optional<Device> deviceOpt = deviceRepository.getByImei(imei);
        if (deviceOpt.isAbsent()) return responseError(SubscriptionErrorEnum.NEW_SMARTPHONE.getId());
        List<Subscription> subscriptionList = subscriptionRepository.findByDeviceId(deviceOpt.get().getId());

        if (subscriptionList.isEmpty())
            return responseError(SubscriptionErrorEnum.NO_SUBSCRIPTION.getId());
        Optional<Subscription> subscriptionOptional = subscriptionRepository.getLastActiveByDeviceId(deviceOpt.get().getId());
        if (subscriptionOptional.get().getStatus() == StatusEnum.EXPIRED) {
            return responseError(SubscriptionErrorEnum.EXPIRED.getId());
        }

        if (deviceOpt.get().getStatus() == StatusEnum.PENDING)
            return responseError(SubscriptionErrorEnum.DEVICE_PENDING.getId());

        if (deviceOpt.get().getAccount() == null)
            return responseError(SubscriptionErrorEnum.DEVICE_NOT_LINKED_ANY_ACCOUNT.getId());

        JSONObject response = new JSONObject();
        response.put("accountName", deviceOpt.get().getAccount().getName());

        response.put("subscriptionInit", subscriptionList.get(0).getServicePlan() != null ? DateUtil.getSimpleDate(subscriptionList.get(0).getStartDate()) : "");
        response.put("subscriptionEnd", subscriptionList.get(0).getServicePlan() != null ? DateUtil.getSimpleDate(subscriptionList.get(subscriptionList.size() - 1).getEndDate()) : "");
        Optional<Position> positionOpt = positionRepository.getLastByDeviceId(deviceOpt.get().getId());
        if (positionOpt.isPresent()) {
            response.put("lastPositionSent", DateUtil.getSimpleDateTime(positionRepository.getLastByDeviceId(deviceOpt.get().getId()).get().getTime()));
            response.put("subscriptionStatus", SubscriptionErrorEnum.OK.getId());
        } else {
            response.put("lastPositionSent", "");
            response.put("subscriptionStatus", SubscriptionErrorEnum.OK.getId());
        }
        log.info("[OK]response -> " + response);
        return Response.ok().entity(response).build();
    }

    private Account getCurrentAccount() {
        Optional<Account> optional = accountDao.getById(identity.getIdentityDto().getAccountId());
        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error("not there are account");
        }
        return null;
    }

    private Response responseItemsShoppingCart(List<Device> deviceList, int itemsOnShoppingCart) {
        JSONObject shoppingCart = new JSONObject();
        shoppingCart.put("status", !deviceList.isEmpty());
        shoppingCart.put("items", itemsOnShoppingCart);
        log.info("response -> " + shoppingCart);
        return Response.ok().entity(shoppingCart).build();
    }

    public String generateOrderId(Subscription subscription, PaymentRequest paymentRequest) {
        String deviceId = subscription.getDevice().getId().toString();
        String subscriptionId = subscription.getId().toString();
        String paymentId = paymentRequest.getId().toString();
        return LongUtil.orderIdToApp(deviceId, subscriptionId, paymentId);
    }

    public String getOrderIdWeb(Long paymentId, int numberPayDevice) {
        String _accountId = identity.getIdentityDto().getAccountId().toString();
        String _paymentId = paymentId.toString();
        String _countDevicesPay = String.valueOf(numberPayDevice);
        String _userId = identity.getIdentityDto().getId().toString();
        return LongUtil.orderIdToWeb(_accountId, _paymentId, _countDevicesPay, _userId);
    }
}