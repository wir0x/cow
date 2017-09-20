
package ch.swissbytes.module.buho.app.device.service;

import ch.swissbytes.domain.dao.AlarmBatteryDao;
import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.dto.DeviceDto;
import ch.swissbytes.domain.dto.DevicesStatusDto;
import ch.swissbytes.domain.dto.SmartphoneDto;
import ch.swissbytes.domain.dto.TransferDeviceDto;
import ch.swissbytes.domain.entities.AlarmBattery;
import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.domain.enumerator.SellerEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.exception.DeviceNotFoundException;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.rest.v1.dto.CurrentPositionDto;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.buho.service.notifications.AlarmService;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.exception.UserNotFoundException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.utils.KeyUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class DeviceService {

    @Inject
    private Logger log;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private ConfigurationRepository configurationRepository;
    @Inject
    private SmsControlDao smsControlDao;
    @Inject
    private GeoFenceRepository fenceRepository;
    @Inject
    private PositionRepository positionRepository;
    @Inject
    private AlarmBatteryDao alarmBatteryDao;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private CoordinateDao coordinateDao;
    @Inject
    private PositionService positionService;
    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private AccountService accountService;
    @Inject
    private AlarmService alarmService;


    public List<Device> listBySellerLogged(Identity identity) {
        if (identity.getIdentityDto().isBuhoAdmin())
            return findAll();

        if (identity.getIdentityDto().isUbidataAdmin())
            return filterByUbidataSeller();

        return Collections.emptyList();
    }

    private List<Device> filterByUbidataSeller() {
        List<Account> accounts = accountService.findBySeller(SellerEnum.UBIDATA);
        List<Device> devices = new ArrayList<>();

        for (Account account : accounts) {
            devices.addAll(deviceRepository.findByAccountId(account.getId()));
        }
        return devices;
    }

    public Optional<Device> getById(Long id) {
        return deviceRepository.getById(id);
    }

    public Device findById(Long id) throws DeviceNotFoundException {
        Optional<Device> deviceOptional = deviceRepository.getById(id);
        if (!deviceOptional.isPresent()) {
            throw new DeviceNotFoundException();
        }
        return deviceOptional.get();
    }

    public void createSosAlarmByUniqueId(String uniqueId) {
        Device device = deviceByUniqueId(uniqueId);
        Optional<Position> position = positionRepository.getLastByDeviceId(device.getId());
        if (position.isPresent()) {
            alarmService.addByPosition(position.get());
        }
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device transferDevice(TransferDeviceDto dto) {
        Device device = deviceById(dto.getDeviceId());
        Account account = accountService.getByEmail(dto.getEmailAccount());
        executeTransfer(device, account);
        return device;
    }

    private void executeTransfer(Device device, Account account) {
        unlinkDeviceOfOldUsers(device);
        setNewAccountToDevice(device, account);
        linkDeviceToUserAdminOfNewAccount(device, account);
    }

    private void unlinkDeviceOfOldUsers(Device device) {
        userDeviceService.unlinkDeviceByDeviceId(device.getId());
    }

    private void setNewAccountToDevice(Device device, Account account) {
        device.setAccount(account);
        deviceRepository.merge(device);
    }

    private void linkDeviceToUserAdminOfNewAccount(Device device, Account account) {
        final User userAdmin = userService.getUserAdminByAccountId(account.getId());
        userDeviceService.linkingDeviceWithUser(device, userAdmin);
    }

    public List<DevicesStatusDto> checkStatusDevices(Identity identity) {
        if (identity.getIdentityDto().isBuhoAdmin())
            return devicesStatusDtoFrom(findAll());

        if (identity.getIdentityDto().isUbidataAdmin())
            return devicesStatusDtoFrom(devicesUbidata());

        return Collections.emptyList();
    }

    private List<DevicesStatusDto> devicesStatusDtoFrom(List<Device> devices) {
        List<DevicesStatusDto> dtos = new ArrayList<>();
        for (Device device : devices) {
            final Optional<Subscription> subscription = subscriptionRepository.getLastActiveByDeviceId(device.getId());
            final Optional<Position> position = positionRepository.getLastByDeviceId(device.getId());
            final DevicesStatusDto dto = makeStatusDevicesDto(device, subscription, position);
            dtos.add(dto);
        }
        return dtos;
    }

    private List<Device> devicesUbidata() {
        List<Account> accounts = accountService.ubidataAccounts();
        List<Device> devices = new ArrayList<>();
        for (Account account : accounts)
            devices.addAll(findByAccoundId(account.getId()));
        return devices;
    }

    private DevicesStatusDto makeStatusDevicesDto(Device device, Optional<Subscription> subscriptionOpt, Optional<Position> positionOpt) {
        final Subscription subscription = subscriptionOpt.isAbsent() ? Subscription.createNew() : subscriptionOpt.get();
        final Position position = positionOpt.isAbsent() ? Position.createNew() : positionOpt.get();
        return DevicesStatusDto.from(device, subscription, position);
    }

    public Device storeDevice(final Device detachedDevice) throws RuntimeException {
        try {
            return deviceRepository.merge(detachedDevice);

        } catch (PersistenceException pEx) {
            log.error("[ERROR] PersistenceException: " + pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public DeviceType storeDeviceType(DeviceType detachedDeviceType) throws RuntimeException {
        try {
            if (detachedDeviceType.isNew())
                detachedDeviceType = deviceRepository.save(detachedDeviceType);
            else
                detachedDeviceType = deviceRepository.merge(detachedDeviceType);

            return detachedDeviceType;

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public String storeDevice(List<Device> detachedDeviceList) {
        String errorResult = "";
        for (Device detachedDevice : detachedDeviceList) {
            try {

                detachedDevice = storeDevice(detachedDevice);
                userDeviceService.storeToAccountAdminByDevice(detachedDevice);

            } catch (EJBException ejbEx) {
                InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
                log.error(ejbEx.getMessage(), ejbEx);
                errorResult = errorResult + detachedDevice.getName() + ": " + invalidInputException.getMessage();
                errorResult = errorResult + "\n";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorResult = errorResult + detachedDevice.getName() + ": " + e.getMessage();
                errorResult = errorResult + "\n";
            }
        }
        return errorResult;
    }

    public List<DeviceDto> findByUserId(Long userId) throws UserNotFoundException {
        return userService.isSystemAdmin(userId) ? deviceToAdmin() : deviceToUser(userId);
    }

    public List<Device> findByAccoundId(Long id) {
        return deviceRepository.findByAccountId(id);
    }

    public List<CurrentPositionDto> currentPositionOfDevicesByUserId(Long id) {
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(id);
        List<CurrentPositionDto> dtoList = new ArrayList<>();

        for (UserDevice userDevice : userDevices) {
            final Optional<Position> position = positionRepository.getLastByDeviceId(userDevice.getDevice().getId());
            final Subscription subscription = subscriptionService.getByDeviceId(userDevice.getDevice().getId());

            if (position.isPresent())
                dtoList.add(CurrentPositionDto.from(userDevice.getDevice(), position.get(), subscription));
            else
                dtoList.add(CurrentPositionDto.withoutPosition(userDevice.getDevice(), subscription));
        }
        return dtoList;
    }

    public CurrentPositionDto getCurrentPositionByDeviceId(Device device) {
        Optional<Position> position = positionRepository.getLastByDeviceId(device.getId());
        return CurrentPositionDto.fromByDeviceAndPosition(device, position);
    }

    private List<DeviceDto> deviceToAdmin() {
        return fromDeviceList(deviceRepository.findAll());
    }

    private List<DeviceDto> deviceToUser(Long userId) {
        List<UserDevice> userDeviceList = userDeviceService.findByUserId(userId);
        List<Device> devicesList = userDeviceList.stream().map(UserDevice::getDevice).collect(Collectors.toList());
        return fromDeviceList(devicesList);
    }


    private List<DeviceDto> fromDeviceList(List<Device> deviceList) {
        return deviceList.stream().map(DeviceDto::from).collect(Collectors.toList());
    }

    public Response delete(Device device) {
        if (device.getAccount() != null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Este dispostivo esta vinculado a una cuenta: "
                    + device.getAccount().getName()).build();
        unlinkDevice(device);
        return Response.ok().build();
    }


    public Device storeNewSmartphone(Account account, Long userId, String deviceName, Device device) {
        device.setName(deviceName);
        device.setDeviceType(new DeviceType(configurationRepository.getSmartphoneTypeId()));
        device.setAccount(account);
        Device storedDevice = storeDevice(device);

        UserDevice userDevice = new UserDevice();
        userDevice.setDevice(new Device(storedDevice.getId()));
        userDevice.setUser(new User(userId));
        userDeviceService.storeUserDevice(userDevice);

        return storedDevice;
    }

    public Device storeDeviceTracker(SmartphoneDto smartphoneDto) {
        Optional<Device> deviceOpt = deviceRepository.getByImei(smartphoneDto.getImei());
        return deviceOpt.isPresent() ? updateSmartPhone(deviceOpt, smartphoneDto) : createSmartPhone(smartphoneDto);
    }

    private Device updateSmartPhone(Optional<Device> deviceOptional, SmartphoneDto smartphoneDto) {
        log.info("update smartPhone: " + deviceOptional.get().getId());
        Device device = deviceOptional.get();
        if (device.getGeneratedId() == null || device.getGeneratedId().isEmpty()) {
            device.setGeneratedId(generateTrackerId());
        }
        device.setGcmToken(smartphoneDto.getGcmToken());
        device.setModificationDate(new Date());
        return storeDevice(device);
    }

    private Device createSmartPhone(SmartphoneDto smartphoneDto) {
        log.info("create smartPhone: " + smartphoneDto.getImei());
        Device device = Device.createSmarthPhone(smartphoneDto.getImei(), smartphoneDto.getGcmToken());
        device.setGeneratedId(generateTrackerId());
        return storeDevice(device);
    }

    public Device linkingDevice(Long userId, String imei) {
        Device device = Device.createNew();
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId);
        log.info("userDevice size: " + userDevices.size());

        if (userDevices.size() > 0 && userDevices.get(0).getDevice().getStatus().equals(StatusEnum.PENDING)) {
            device = userDevices.get(0).getDevice();
            device.setImei(imei);
            device.setStatus(StatusEnum.ENABLED);
            device = storeDevice(device);
        }
        log.info("linking device? -> " + device.getId());
        return device;
    }


    public String generateTrackerId() {
        int min = KeyAppConfiguration.getInt(ConfigurationKey.MIN_DIGIT_TRACKER_ID),
                max = KeyAppConfiguration.getInt(ConfigurationKey.MAX_DIGIT_TRACKER_ID);
        String trackerId;
        Optional<Device> device;

        do {
            trackerId = KeyUtil.random(min, max);
            device = deviceRepository.findByTrackerId(trackerId);
        } while (device.isPresent());

        return trackerId;
    }

    public Response updatePhoneNumber(Device device, String phoneNumber) {
        if (!LongUtil.isNumeric(phoneNumber)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("telefono no valido").build();
        }

        device.setPhoneNumber(phoneNumber);
        deviceRepository.merge(device);
        return Response.ok(device.getPhoneNumber()).build();
    }


    public void linkingSmartphone(Long userId, String imei) {

        Optional<Device> deviceOptional = deviceRepository.getByImei(imei);
        Optional<User> userEntityOptional = userRepository.getById(userId);

        if (deviceOptional.isPresent() && userEntityOptional.isPresent()) {
            Device device = deviceOptional.get();
            User user = userEntityOptional.get();

            // Set account to smartphone and store.
            device.setAccount(user.getAccount());
            device.setName(user.getName());
            device = storeDevice(device);

            // Linking smartphone with user.
            userDeviceService.storeUserDevice(new UserDevice(user, device));

        } else {

            log.warn(String.format("smartphone: %s or user: %s  not exist ", imei, userId));
        }

    }


    /**
     * Unlink device - delete all relation with other entities
     *
     * @param device
     */
    public void unlinkDevice(Device device) {
        Long deviceId = device.getId();

        // Find all sms control and Remove
        List<SmsControl> smsControlList = smsControlDao.findByDeviceId(deviceId);
        log.info("deleting sms control: " + smsControlList.size());
        if (!smsControlList.isEmpty()) smsControlDao.delete(smsControlList);

        // Find all fence and Delete
        List<GeoFence> geoFenceList = fenceRepository.findByDeviceId(deviceId);
        log.info("deleting fence: " + geoFenceList.size());
        if (!geoFenceList.isEmpty()) {
            List<Coordinate> coordinateList = coordinateDao.findByFenceList(geoFenceList);
            coordinateDao.delete(coordinateList);
            fenceRepository.delete(geoFenceList);
        }

        // Find all position and Delete
        List<Position> positionList = positionRepository.findByDeviceId(deviceId);
        log.info("deleting position: " + positionList.size());
        if (!positionList.isEmpty()) positionRepository.delete(positionList);

        // Find all subscription and Delete
        subscriptionService.deleteExceptFreeSubscriptionByDeviceID(deviceId);

        // Find all user device and Delete
        List<UserDevice> userDeviceList = userDeviceRepository.findByDeviceId(deviceId);
        log.info("deleting user device: " + userDeviceList.size());
        if (!userDeviceList.isEmpty()) userDeviceRepository.delete(userDeviceList);

        // Get alarm battery and remove
        Optional<AlarmBattery> alarmBatteryOpt = alarmBatteryDao.getByDeviceId(deviceId);
        if (alarmBatteryOpt.isPresent()) alarmBatteryDao.remove(alarmBatteryOpt.get());

        // Update device - set null on field account, set empty in name
        log.info("disabled device: " + device.getId());
        device.setAccount(null);
        storeDevice(device);
    }

    public Device deviceById(Long id) {
        Optional<Device> device = deviceRepository.getById(id);

        if (device.isAbsent())
            throw new DeviceNotFoundException();

        return device.get();
    }

    public Device deviceByUniqueId(String uniqueId) {
        Optional<Device> device = deviceRepository.getByImei(uniqueId);

        if (device.isAbsent())
            throw new DeviceNotFoundException();

        return device.get();
    }
}
