package ch.swissbytes.module.buho.app.userdevice.service;

import ch.swissbytes.domain.dto.DeviceBackendDto;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;


@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserDeviceService {

    @Inject
    private Logger log;

    @Inject
    private UserDeviceRepository userDeviceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private DeviceService deviceService;

    public void unlinkDeviceByDeviceId(Long id) {
        List<UserDevice> userDevices = userDeviceRepository.findByDeviceId(id);
        userRepository.delete(userDevices);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUserDevice(UserDevice userDevice) {
        userDeviceRepository.save(userDevice);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUserDeviceStorage(UserDevice userDevice) {
        userDeviceRepository.merge(userDevice);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(UserDevice userDevice) {
        log.info("deleteUserDevice: " + userDevice.getId());
        List<UserDevice> userDevicesOld = userDeviceRepository.findByUserId(userDevice.getUser().getId());
        if (!userDevicesOld.isEmpty()) userDeviceRepository.delete(userDevicesOld);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateList(Long userId, List<UserDevice> userDeviceList) {
        log.info("updateList: " + userDeviceList.size());
        List<UserDevice> userDeviceListOld = userDeviceRepository.findByUserId(userId);
        userDeviceList.stream().filter(userDevice -> !userDeviceListOld.contains(userDevice)).forEach(userDeviceRepository::save);
        userDeviceListOld.stream().filter(userDevice -> !userDeviceList.contains(userDevice)).forEach(userDeviceRepository::remove);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeUserDevice(Long userId) {
        log.info("deleting UserDevice!");
        List<UserDevice> userDeviceListOld = userDeviceRepository.findByUserId(userId);
        if (!userDeviceListOld.isEmpty()) userDeviceRepository.delete(userDeviceListOld);
    }

    public void storeToAccountAdminByDevice(Device device) {
        log.info("storeToAccountAdminByDevice: " + device.getId());
        if (device.getAccount() == null) {
            log.error("device: " + device.getId() + "not has account");
            return;
        }
        List<User> userList = userRepository.findAccountAdmin(device.getAccount().getId());
        for (User user : userList) {
            Optional<UserDevice> userDeviceOptional = userDeviceRepository.getByUserIdAndDeviceId(user.getId(), device.getId());
            if (userDeviceOptional.isAbsent()) {
                UserDevice userDevice = new UserDevice();
                userDevice.setUser(user);
                userDevice.setDevice(device);
                userDeviceRepository.merge(userDevice);
            }
        }
    }

    public void delete(List<UserDevice> userDeviceList) {
        log.info("delete: " + userDeviceList.size());
        for (UserDevice userDevice : userDeviceList) {
            log.info("remove device: " + userDevice.getDevice().getId());
        }
        if (!userDeviceList.isEmpty()) userDeviceRepository.delete(userDeviceList);
    }

    public List<UserDevice> findByUserId(Long userId) {
        return userDeviceRepository.findByUser(userId);
    }

    public List<UserDevice> findByDeviceId(Long id) {
        return userDeviceRepository.findByDevice(id);
    }

    public void deleteByUserId(Long userId) {
        List<UserDevice> userDeviceList = userDeviceRepository.findByUserId(userId);
        delete(userDeviceList);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteUserDeviceByUserList(List<User> userList) {
        log.info("removeUserDevice: " + userList.size());
        List<UserDevice> userDeviceListOld = userDeviceRepository.findByUserList(userList);
        if (!userDeviceListOld.isEmpty()) userDeviceRepository.delete(userDeviceListOld);
    }

    public UserDevice storeUserDevice(final UserDevice detachedUserDevice) throws RuntimeException {
        try {
            log.info(String.format("storeUserDevice: %s", detachedUserDevice));
            Optional<UserDevice> userDeviceOptional = userDeviceRepository.getByUserIdAndDeviceId(detachedUserDevice.getUser().getId(), detachedUserDevice.getDevice().getId());

            UserDevice userDevice;
            if (userDeviceOptional.isPresent()) {
                userDevice = userDeviceOptional.get();
                log.info("UserDevice present: " + userDevice.getId());
                return userDevice;
            }

            if (detachedUserDevice.isNew()) {
                userDevice = userDeviceRepository.save(detachedUserDevice);
            } else {
                userDevice = userDeviceRepository.merge(detachedUserDevice);
            }
            log.info(String.format("user_device created!: %s", userDevice));
            return userDevice;

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public void setDeviceToUserAdmin(Device device) {
        List<User> userList = userRepository.findAccountAdmin(device.getAccount().getId());
        userDeviceRepository.merge(new UserDevice(userList.get(0), device));
    }

    public void updateAccountDevices(Account account, DeviceBackendDto[] deviceBackendDtoList) {
        List<Device> newDeviceList = getDeviceList(deviceBackendDtoList);
        deviceService.storeDevice(newDeviceList);
        removeOldDeviceList(account, newDeviceList);
    }

    private List<Device> getDeviceList(DeviceBackendDto[] deviceBackendDtoList) {
        List<Device> deviceList = new ArrayList<>();
        for (DeviceBackendDto deviceBackendDto : deviceBackendDtoList) {
            Optional<Device> deviceOptional = deviceRepository.getById(deviceBackendDto.getId());
            if (deviceOptional.isPresent()) deviceList.add(deviceBackendDto.toDevice(deviceOptional));
        }
        return deviceList;
    }

    private void removeOldDeviceList(Account account, List<Device> newDeviceList) {
        List<Device> oldDeviceList = deviceRepository.findByAccountId(account.getId());
        List<UserDevice> userDeviceList = new ArrayList<>();

        oldDeviceList.stream().filter(device -> !newDeviceList.contains(device)).forEach(device -> {
            device.setAccount(null);
            deviceService.storeDevice(device);
            userDeviceList.addAll(userDeviceRepository.findByDeviceId(device.getId()));
        });
        delete(userDeviceList);
    }

    public UserDevice linkingDeviceWithUser(Device device, User user) {
        Optional<UserDevice> userDeviceOptional = userDeviceRepository.getByUserIdAndDeviceId(user.getId(), device.getId());
        if (userDeviceOptional.isAbsent()) {
            return userDeviceRepository.merge(new UserDevice(user, device));
        }
        return userDeviceRepository.merge(userDeviceOptional.get());
    }
}
