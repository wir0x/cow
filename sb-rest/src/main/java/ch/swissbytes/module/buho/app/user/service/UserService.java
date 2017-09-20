package ch.swissbytes.module.buho.app.user.service;

import ch.swissbytes.domain.dao.VerificationTokenDao;
import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.ChangePasswordDto;
import ch.swissbytes.domain.dto.UserManagementDto;
import ch.swissbytes.domain.entities.VerificationTokenEntity;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.role.repository.RoleRepository;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.app.userrole.respository.UserRoleRepository;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import ch.swissbytes.module.shared.exception.*;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import ch.swissbytes.module.shared.utils.Encode;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Stateless
public class UserService {

    private final long roleSystemAdmin = 1;
    private final long roleAccountAdmin = 2;
    private final long roleAdmin = 3;
    private final long roleTracker = 5;

    @Inject
    private AppConfiguration labels;
    @Inject
    public Validator validator;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private Logger log;
    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private VerificationTokenDao verificationTokenDao;
    @Inject
    private AccountService accountService;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private Identity identity;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private ViewerDao viewerDao;
    @Inject
    private DeviceService deviceService;
    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private UserRoleService userRoleService;


    public User add(User user) {
        return userRepository.merge(user);
    }

    public User getUserAdminByAccountId(Long id) {
        return userRepository.findByAccountId(id).get(0);
    }

    public void validateUser(User detachedUser) throws InvalidInputException {
        log.info("validateUser " + detachedUser);
        if (!detachedUser.isNew() && isAccountAdmin(detachedUser.getId()) && !isSystemAdmin()) {
            Optional<User> userEntityOptional = userRepository.getById(detachedUser.getId());

            if (userEntityOptional.isAbsent()) throw new InvalidInputException("Usuario no existe");

            if (!isChangingUserName(detachedUser, userEntityOptional.get()))
                throw new InvalidInputException("No tiene permisos para cambiar el nombre de usuario al dueno de la cuenta.");

            if (!isChangingUserRole(detachedUser, userEntityOptional.get()))
                throw new InvalidInputException("No tiene permisos para cambiar el rol al dueno de la cuenta.");
        }

        Optional<User> userEntityOptional = userRepository.getByUsername(detachedUser.getUsername());

        if (userEntityOptional.isPresent() && !userEntityOptional.get().getId().equals(detachedUser.getId())) {
            if (LongUtil.isNumeric(userEntityOptional.get().getUsername())) {
                throw new InvalidInputException("El número de telefono ya se encuentra registrado");
            }
            throw new InvalidInputException("El nombre de usuario ya se encuentra registrado");
        }

        userEntityOptional = userRepository.getByEmail(detachedUser.getEmail());
        if (userEntityOptional.isPresent() && userEntityOptional.get().getId() != detachedUser.getId()) {
            throw new InvalidInputException("El correo electronico ya se encuentra registrado");
        }
    }

    public User storeUser(User detachedUser) throws RuntimeException {
        try {
            User user = getUserEntity(detachedUser);
            user = add(user);
            log.info(String.format("user: %s create!", user.getId()));
            return user;

        } catch (EJBException ejbEx) {
            log.error(ejbEx.getMessage(), ejbEx);
            throw new InvalidInputException(ejbEx);
        }
    }

    public void update(User user) throws UsernameAlreadyExistException, EmailAlreadyExistException, FieldNotValidException {
        validateEmail(user);
        validateUserName(user);
        add(user);
    }

    private void validateUserEntity(User user) {
        ValidationUtil.validateEntityFields(validator, user);
    }

    private void validateUserName(User userEntity) {
        final Optional<User> user = userRepository.findByUserName(userEntity.getUsername());
        if (user.isPresent() && !Objects.equals(userEntity.getId(), user.get().getId()))
            throw new UsernameAlreadyExistException();
    }

    private void validateEmail(User userEntity) {
        Optional<User> user = userRepository.getByEmail(userEntity.getEmail());
        if (user.isPresent() && !Objects.equals(user.get().getId(), userEntity.getId()))
            throw new EmailAlreadyExistException();
    }

    public void updateFrom(UserManagementDto[] userManagementDtoList) throws UsernameAlreadyExistException, EmailAlreadyExistException, FieldNotValidException {
        List<User> userList = new ArrayList<>();

        for (UserManagementDto userManagementDto : userManagementDtoList) {
            final Long userId = userManagementDto.getId();
            User user = userManagementDto.toUserEntity();
            user.setAccount(new Account(identity.getIdentityDto().getAccountId()));
            userList.add(user);
            userRoleService.updateRolUser(new UserRole(userId, userManagementDto.getRoleId()));
            userDeviceService.deleteByUserId(userId);
        }
        storeUser(userList);
    }

    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isAbsent())
            throw new UserNotFoundException();
        return userEntityOptional.get();
    }

    private User getUserEntity(User user) {
        validateUser(user);

        if (user.getChangePassword()) {
            user.setGeneratedPassword(user.getPassword());
        } else {
            user.setGeneratedPassword("");
        }

        if (user.isNew()) {
            String passwordEncoded = Encode.encode(user.getPassword());
            user.setPassword(passwordEncoded);
        }
        return user;

    }

    public User storeUser(User detachedUser, List<UserDevice> userDeviceList) throws RuntimeException {
        log.info(String.format("StoreUser: user: %s deviceList %s ", detachedUser.getName(), userDeviceList.size()));
        try {
            if (detachedUser.isNew()) {
                validateUser(detachedUser);
                String passwordEncoded = Encode.encode(detachedUser.getPassword());
                detachedUser.setPassword(passwordEncoded);
            } else {
                Optional<User> userEntity = userRepository.getById(detachedUser.getId());
                detachedUser.setPassword(userEntity.get().getPassword());
            }

            detachedUser = add(detachedUser);

            for (UserDevice userDevice : userDeviceList) {
                userDevice.setUser(detachedUser);
            }

            userDeviceService.updateList(detachedUser.getId(), userDeviceList);
            return detachedUser;

        } catch (EJBException ejbEx) {
            log.error(ejbEx.getMessage(), ejbEx);
            throw new InvalidInputException(ejbEx);
        }
    }

    private String storeUser(List<User> detachedUserList) {
        log.info(String.format("StoreUser | userList %s", detachedUserList.size()));
        String errorResult = "";
        for (User detachedUser : detachedUserList) {
            try {
                Optional<User> userOptional = userRepository.getById(detachedUser.getId());
                if (userOptional.isAbsent())
                    throw new InvalidInputException("El usuario no existe");
                detachedUser = storeUser(detachedUser, detachedUser.getUserDeviceList());

            } catch (EJBException ejbEx) {
                InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
                log.error(ejbEx.getMessage(), ejbEx);
                errorResult = errorResult + detachedUser.getName() + ": " + invalidInputException.getMessage();
                errorResult = errorResult + "\n";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                errorResult = errorResult + detachedUser.getName() + ": " + e.getMessage();
                errorResult = errorResult + "\n";
            }
        }
        return errorResult;
    }

    public User changePassword(User detachedUser) throws EJBException {
        log.info(String.format("ChangePassword | user: %s ", detachedUser.getName()));
        if (detachedUser.isNew())
            throw new InvalidInputException("No existe el usuario con el id: " + detachedUser.getId());

        if (!isSystemAdmin() && detachedUser.getAccount() != null
                && accountService.isTestAccount(detachedUser.getAccount().getId()))
            throw new InvalidInputException("No se puede modificar la contrasena de la cuenta de Test");

        // parameters to change password
        detachedUser.setChangePassword(true);
        detachedUser.setGeneratedPassword(detachedUser.getPassword());
        return storePassword(detachedUser.getPassword(), detachedUser);
    }

    public User resetPasswordBySysAdmin(String password, User detachedUser) {
        return storePassword(password, detachedUser);
    }

    private User storePassword(String password, User user) {
        String passwordEncode = Encode.encode(password);
        user.setPassword(passwordEncode);
        return add(user);
    }

    private User changePasswordFirstLogin(User detachedUser) throws EJBException {
        log.info(String.format("ChangePassword | user: %s ", detachedUser.getName()));
        if (detachedUser.isNew())
            throw new InvalidInputException("No existe el usuario con el id: " + detachedUser.getId());

        detachedUser.setChangePassword(false);
        detachedUser.setGeneratedPassword("");

        String passwordEncoded = Encode.encode(detachedUser.getPassword());
        log.info("password : " + detachedUser.getPassword());
        detachedUser.setPassword(passwordEncoded);

        User user = add(detachedUser);
        log.info("UserEntity password changed: " + user.getUsername());
        return user;
    }


    public User changePassword(ChangePasswordDto dto) throws EJBException {
        log.info("changePassword => " + dto);

        if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new InvalidInputException(labels.getMessage("passwords.are.not.equals"));

        Optional<User> userOptional = userRepository.getById(dto.getUserId());

        if (userOptional.isAbsent())
            throw new InvalidInputException(labels.getMessage("user.not.exist"));

        User user = userOptional.get();
        user.setPassword(dto.getNewPassword());
        return changePasswordFirstLogin(user);
    }

    public User changePasswordWithToken(User detachedUser, VerificationTokenEntity tokenEntity) throws EJBException {
        log.info(String.format("ChangePassword | user: %s verificationToken: %s ", detachedUser.getName(), tokenEntity.getId()));
        if (detachedUser.isNew()) {
            throw new InvalidInputException("No existe el usuario con el id: " + detachedUser.getId());
        }

        String passwordEncoded = Encode.encode(detachedUser.getPassword());
        detachedUser.setPassword(passwordEncoded);

        User user = add(detachedUser);

        tokenEntity.setVerified(true);
        userRepository.merge(tokenEntity);

        log.info("UserEntity password changed: " + user.getUsername());
        return user;
    }

    public void delete(List<User> userList) {
        userList.forEach(this::delete);
    }

    public void delete(User user) {
        Long userId = user.getId();

        List<UserRole> userRoleList = userRoleRepository.findByUserId(userId);
        if (!userRoleList.isEmpty()) userRoleRepository.delete(userRoleList);

        List<UserDevice> userDeviceList = userDeviceRepository.findByUserId(userId);
        if (!userDeviceList.isEmpty()) userDeviceRepository.delete(userDeviceList);

        List<VerificationTokenEntity> verificationTokenList = verificationTokenDao.findByUserId(userId);
        if (!verificationTokenList.isEmpty()) verificationTokenDao.delete(verificationTokenList);

        List<Viewer> viewerList = viewerDao.findByUserIdToDelete(userId);
        if (!viewerList.isEmpty()) viewerDao.delete(viewerList);

        subscriptionService.removeUserOfSubscription(userId);
        userRepository.remove(user);
        log.info("user: " + userId + " removed!");
    }

    private void removeVerificationToken(Long userId) {
        log.info(String.format("removeVerificationToken | userId: %s ", userId));
        List<VerificationTokenEntity> verificationTokenList = verificationTokenDao.findByUserId(userId);

        for (VerificationTokenEntity verificationTokenEntity : verificationTokenList) {
            verificationTokenDao.remove(verificationTokenEntity);
        }
        log.info("Verification token  removed by id: " + userId);
    }

    public User validateEmail(String mail) throws RuntimeException {
        log.info(String.format("ValidateEmail | mail: %s", mail));

        if (StringUtil.isEmpty(mail)) {
            log.warn("email is empty!");
            throw new InvalidInputException("El correo electrónico es requerido");
        }

        Optional<User> entityOptional = userRepository.getByEmail(mail);
        if (entityOptional.isAbsent()) {
            log.warn("Does not exist email!");
            throw new InvalidInputException("No se pudo encontrar el correo electrónico proporcionado");
        }

        return entityOptional.get();
    }

    public List<User> findByAccountId(Long id) {
        return userRepository.findByAccountId(id);
    }

    public List<User> findByUserId(Long userId) {
        Optional<User> userOptional = userRepository.getById(userId);
        if (userOptional.isAbsent()) {
            throw new InvalidInputException("User not found");
        }

        if (isSystemAdmin(userId)) {
            return userRepository.findAll();
        }

        Long accountId = userOptional.get().getAccount().getId();
        return userRepository.findByAccountId(accountId);
    }


    public boolean isSystemAdmin() {
        return isSystemAdmin(identity.getIdentityDto().getId());
    }

    public boolean isAccountAdmin(Long userId) {
        List<UserRole> userRoleList = userRoleRepository.findByUserId(userId);
        for (UserRole userRole : userRoleList) {
            if (userRole.getRole().getId().equals(roleAccountAdmin)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSystemAdmin(Long userId) {
        List<UserRole> userRoleList = userRoleRepository.findByUserId(userId);
        for (UserRole userRole : userRoleList) {
            if (userRole.getRole().getId().equals(roleSystemAdmin)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRoleTracker(Long userId) {
        List<UserRole> userRoleList = userRoleRepository.findByUserId(userId);
        for (UserRole userRole : userRoleList)
            if (userRole.getRole().getId().equals(roleTracker))
                return true;
        return false;
    }

    public boolean isLinkedUser(Long userId, String uniqueId) {
        if (hasRoleTracker(userId)) {
            List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId);
            if (userDevices.size() > 0) {
                Optional<Device> deviceOpt = deviceRepository.getByImei(uniqueId);
                if (userDevices.get(0).getDevice().getStatus().equals(StatusEnum.PENDING) && deviceOpt.isAbsent()) {
                    return false;
                }

                if (deviceOpt.isPresent()) {
                    if (userDevices.get(0).getDevice().getId().equals(deviceOpt.get().getId())) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verify if the device it's linking to one account
     * NOTE: uniqueId in android is the Imei for ios is UniqueId
     *
     * @param userId
     * @param uniqueId
     * @return
     */
    public boolean isLinkedDevice(Long userId, String uniqueId) {
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId);
        Optional<Device> deviceOpt = deviceRepository.getByImei(uniqueId);

        if (deviceOpt.isPresent() && deviceOpt.get().getAccount() == null) {
            deviceRepository.remove(deviceOpt.get());
        } else if (deviceOpt.isPresent()) {
            Optional<UserDevice> userDeviceOpt = userDeviceRepository.getByUserIdAndDeviceId(userId, deviceOpt.get().getId());
            return userDeviceOpt.isAbsent();
        }

        for (UserDevice userDevice : userDevices) {
            Device device = userDevice.getDevice();
            if (device.getStatus().equals(StatusEnum.ENABLED))
                return true;
        }
        return false;
    }

    public Boolean isChangingUserName(User detachedUser, User userOptional) {
        return detachedUser.getUsername().equals(userOptional.getUsername());
    }

    public Boolean isChangingUserRole(User detachedUser, User user) {
        Role roleDetached = userRoleRepository.getByUserId(detachedUser.getId()).get().getRole();
        Role roleOptional = userRoleRepository.getByUserId(user.getId()).get().getRole();
        return roleDetached.getId().equals(roleOptional.getId());
    }

    private void validateFields(User user) {
        ValidationUtil.validateEntityFields(validator, user);
    }
}
