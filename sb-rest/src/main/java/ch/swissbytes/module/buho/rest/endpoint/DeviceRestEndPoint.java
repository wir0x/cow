package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.*;
import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.buho.app.account.exception.AccountNotFoundException;
import ch.swissbytes.module.buho.app.device.exception.DeviceNotFoundException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.exception.UserNotFoundException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import net.minidev.json.JSONObject;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("device")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class DeviceRestEndPoint {

    @Inject
    private Logger log;
    @Inject
    private DeviceService deviceService;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private Identity identity;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private ViewerService viewerService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ViewerDao viewerDao;
    @Inject
    private AccountRepository accountDao;
    @Inject
    private SubscriptionRepository subscriptionRepository;


    @POST
    @Path("/transfer")
    public Response transferDeviceToAnotherAccount(TransferDeviceDto dto) {
        try {
            log.info("******  TRANSFER DEVICE TO ANOTHER ACCOUNT ******");
            log.info("dto: " + dto);
            deviceService.transferDevice(dto);
            return Response.ok().build();

        } catch (AccountNotFoundException e) {
            log.error("transfer device to another account not found: " + dto.getEmailAccount());
            return Response.status(Status.NOT_FOUND).entity("No se encontro una cuenta con el correo electrónico: " + dto.getEmailAccount()).build();
        } catch (DeviceNotFoundException e) {
            log.error("Transfer device to another account: device not found " + dto.getDeviceId());
            return Response.status(Status.NOT_FOUND).entity("No se encontro el dispositivo con el id: " + dto.getDeviceId()).build();
        }
    }

    @POST
    @Path("/{uniqueId}/SOS")
    public Response SosAlarm(@PathParam("uniqueId") String uniqueId) {
        try {
            log.info("**** SOS Alarm");
            log.info("uniqueId: " + uniqueId);

            deviceService.createSosAlarmByUniqueId(uniqueId);
            return Response.ok().build();

        } catch (DeviceNotFoundException e) {
            log.error("No se encontro el dispositivo: " + uniqueId);
            return Response.status(Status.NOT_FOUND).entity("Dispositivo no encontrado: " + uniqueId).build();
        }
    }

    @GET
    @Path("/list")
    @LoggedIn
    public Response getDevices() {
        log.info("*************** GET DEVICE LIST TO USER LOGGED ****************");
        try {

            Long userId = identity.getIdentityDto().getId();
            List<DeviceDto> deviceDtoList = deviceService.findByUserId(userId);
            return Response.ok().entity(deviceDtoList).build();


        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (RuntimeException e) {
            log.error("[ERROR] RuntimeException: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{uniqueId}")
    public Response statusDevice(@PathParam("uniqueId") String uniqueId) {
        try {

            log.info("************ CHECK DEVICE STATUS **********");
            log.info("uniqueId: " + uniqueId);

            Optional<Device> deviceOpt = deviceRepository.getByImei(uniqueId);
            if (deviceOpt.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            Optional<Subscription> subscriptionOpt = subscriptionRepository.getActiveByDeviceId(deviceOpt.get().getId());
            Boolean hasSubscription = subscriptionOpt.isPresent();
            JSONObject response = new JSONObject();
            response.put("isLinked", deviceOpt.isPresent());
            response.put("hasSubscription", hasSubscription);
            log.info("response: " + response);
            return Response.ok().entity(response).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.ok().status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/list/admin")
    @RolesAllowed({"user-management"})
    public Response findAllDevice() {
        try {
            log.info("*********** FIND DEVICES OF ADMIN ACCOUNT *********** ");

            // Get device list by account
            List<Device> deviceList = deviceRepository.findByAccountId(identity.getIdentityDto().getAccountId());

            // Convert device list to device DTO list
            List<DeviceDto> deviceDtoList = fromDeviceList(deviceList);

            return Response.ok().entity(deviceDtoList).build();

        } catch (RuntimeException e) {

            log.error("[ERROR] RuntimeException: " + e.getMessage());
            return Response.serverError().build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/backend/list")
    @RolesAllowed({"device-management", "account-management"})
    public List<DeviceBackendDto> getDevicesBackend() {
        log.info("********** Backend | Find all devices **********");
        List<Device> devices = deviceService.listBySellerLogged(identity);
        return fromDeviceListBackend(devices);
    }

    @GET
    @Path("/backend/list/free")
    public List<DeviceBackendDto> getFreeDevices() {
        log.info("********** Find All free devices ************");
        return fromFreeDevicesList(deviceRepository.findAll());
    }

    @GET
    @Path("/backend/list/no-free")
    public List<DeviceBackendDto> getNoFreeDevices() {
        log.info("********** Find no free devices **********");
        return fromNoFreeDeviceList(deviceRepository.findAll());
    }

    @GET
    @Path("/backend/list/device-type")
    public Response findDeviceType() {
        try {

            log.info("********** Find Device Type *********");
            List<DeviceType> deviceTypesList = deviceRepository.findAllDeviceType();
            return Response.ok().entity(deviceTypesList).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception:" + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/backend/list/{accountId}")
    public List<DeviceBackendDto> FindDeviceListByCompany(@PathParam("accountId") Long accountId) {
        log.info("********* Find Device List By Account Id **********");
        log.info("accountId: " + accountId);
        return fromNoFreeDeviceList(deviceRepository.findByAccountId(accountId));
    }

    @POST
    @Path("smartphone")
    public Response registerSmartphone(SmartphoneDto smartphoneDto) {

        log.info("************* REGISTER SMARTPHONE *************");

        Device device = deviceService.storeDeviceTracker(smartphoneDto);
        TrackerDeviceDto trackerDeviceDto = new TrackerDeviceDto(device.getId(), device.getGeneratedId());
        log.info("trackerDeviceDto: " + trackerDeviceDto);
        return Response.ok(trackerDeviceDto).build();
    }

    @POST
    @Path("smartphone/linking")
    public Response linkingSmartphone(AddSmartphoneDto addSmartphoneDto) {

        log.info("************* LINKING SMARTPHONE *************");
        log.info("input: " + addSmartphoneDto);

        Optional<Device> deviceOptional = deviceRepository.getByGeneratedId(addSmartphoneDto.getGeneratedId());

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).entity("Generated id not found").build();
        }

        Device device = deviceOptional.get();

        if (device.getAccount() != null) {
            return Response.status(Status.NOT_ACCEPTABLE).entity("device it's linked").build();
        }

        // Update device with, add the new name, linking to account and device.
        device.setName(addSmartphoneDto.getName());
        device.setAccount(new Account(identity.getIdentityDto().getAccountId()));

        device = deviceService.storeDevice(device);
        User user = new User(identity.getIdentityDto().getId());

        userDeviceService.linkingDeviceWithUser(device, user);
        DeviceDto deviceDto = DeviceDto.from(device);

        return Response.ok(deviceDto).build();
    }


    @POST
    @Path("/smart/set/gcm")
    public Response storeDevice(SmartphoneDto smartphoneDto) {
        try {

            log.info("*********** STORE DEVICE AND SET GCM TOKEN [AppTracker] ************");
            log.info("SmartphoneDto: " + smartphoneDto);

            deviceService.storeDeviceTracker(smartphoneDto);
            return Response.ok().build();

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (RuntimeException e) {

            log.error("[ERROR] RuntimeException: " + e.getMessage(), e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/smart/set/gcm/viewer")
    public Response storeViewerDevice(SmartphoneDto smartphoneDto) {
        try {

            log.info("*********** Store Viewer Device [AppViewer] *********");
            log.info("SmartphoneDto: " + smartphoneDto);

            viewerService.storeViewer(smartphoneDto);
            return Response.created(URI.create("")).build();

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (RuntimeException e) {

            log.error("[ERROR] RuntimeException: " + e.getMessage(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/backend/create")
    @RolesAllowed({"device-management"})
    public Response createDevice(DeviceBackendDto deviceBackendDto) {
        try {
            log.info("********** Backend | Create Device **********");
            log.info("DeviceBackendDto: " + deviceBackendDto);
            Optional<Device> deviceOptional = deviceRepository.getById(deviceBackendDto.getId());
            Device device = deviceBackendDto.deviceEntityFromDeviceDtoBackend(deviceOptional);
            Long deviceId = deviceService.storeDevice(device).getId();
            return Response.ok(deviceId).build();

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/backend/update/{accountId}")
    @RolesAllowed({"device-config", "account-management"})
    public Response updateDevicesAssignment(@PathParam("accountId") Long accountId, DeviceBackendDto[] deviceBackendDtoList) {
        try {
            log.info("************ Update devices Assignment *************");
            log.info(String.format("accountId: %s deviceList:  %s", accountId, deviceBackendDtoList.length));

            Optional<Account> accountOptional = accountDao.getById(accountId);
            if (accountOptional.isAbsent()) return Response.status(Status.NOT_FOUND).build();

            userDeviceService.updateAccountDevices(accountOptional.get(), deviceBackendDtoList);
            return Response.ok().build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/backend/update-device-type/{deviceTypeId}")
    @RolesAllowed({"device-config", "account-management"})
    public Response updateDevicesAssignment(@PathParam("deviceTypeId") Long deviceTypeId) {
        try {
            log.info("************ Update Device Assignment **************");
            log.info("deviceTypeId: " + deviceTypeId);
            deviceService.storeDeviceType(deviceRepository.getDeviceTypeById(deviceTypeId).get());
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/unlink/{deviceId}")
    @LoggedIn
    public Response unlinkDevice(@PathParam("deviceId") Long deviceId) {
        try {
            log.info("************ UNLINK DEVICE ************");
            log.info("deviceId: " + deviceId);

            Optional<Device> deviceOptional = deviceRepository.getById(deviceId);
            if (deviceOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            deviceService.unlinkDevice(deviceOptional.get());
            return Response.ok().build();

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @RolesAllowed({"device-config"})
    public Response updateDevices(DeviceDto[] deviceDtoList) {
        try {
            log.info("*********** Update devices list ************");
            log.info("devicesList " + deviceDtoList.length);
            List<Device> deviceList = new ArrayList<>();

            for (DeviceDto deviceDto : deviceDtoList) {
                Optional<Device> deviceOptional = deviceRepository.getById(deviceDto.getId());
                if (deviceOptional.isPresent()) {
                    deviceList.add(deviceDto.convertToDevice(deviceOptional));
                }
            }
            String responseError = deviceService.storeDevice(deviceList);

            if (!responseError.isEmpty()) {
                throw new InvalidInputException(responseError);
            }
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

    @PUT
    @Path("/update/{deviceId}/{phoneNumber}")
    public Response updatePhoneNumber(@PathParam("deviceId") Long deviceId,
                                      @PathParam("phoneNumber") String phoneNumber) {

        log.info("**************** Update Phone Number ***************");
        log.info(String.format("deviceId: %s PhoneNumber: %s", deviceId, phoneNumber));

        Optional<Device> deviceOptional = deviceRepository.getById(deviceId);

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return deviceService.updatePhoneNumber(deviceOptional.get(), phoneNumber);
    }

    @DELETE
    @Path("/backend/delete/{deviceId}")
    @RolesAllowed({"device-management"})
    public Response removeDevice(@PathParam("deviceId") Long deviceId) {
        try {

            log.info("*********** Remove device ************");
            log.info("deviceId: " + deviceId);

            Optional<Device> deviceOptional = deviceRepository.getById(deviceId);
            if (deviceOptional.isAbsent()) return Response.status(Status.NOT_FOUND).build();

            deviceService.delete(deviceOptional.get());
            return Response.ok().build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("status")
//    @RolesAllowed("device-management")
    public Response statusDevices() {
        try {
            final List<DevicesStatusDto> dto = deviceService.checkStatusDevices(identity);
            return Response.ok().entity(dto).build();

        } catch (AccountNotFoundException e) {
            log.info("account not found");
            return Response.status(Status.CONFLICT).build();
        } catch (RuntimeException e) {
            log.info("runtime exc");
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private List<DeviceDto> fromDeviceList(List<Device> deviceList) {
        return deviceList.stream().map(DeviceDto::from).collect(Collectors.toList());
    }

    private List<DeviceBackendDto> fromDeviceListBackend(List<Device> deviceList) {
        return deviceList.stream().map(DeviceBackendDto::fromDeviceEntity).collect(Collectors.toList());
    }

    private List<DeviceBackendDto> fromFreeDevicesList(List<Device> deviceList) {
        return deviceList.stream().filter(device -> device.getAccount() == null).map(DeviceBackendDto::fromDeviceEntity).collect(Collectors.toList());
    }

    private List<DeviceBackendDto> fromNoFreeDeviceList(List<Device> deviceList) {
        return deviceList.stream().filter(device -> device.getId() != null).map(DeviceBackendDto::fromDeviceEntity).collect(Collectors.toList());
    }
}
