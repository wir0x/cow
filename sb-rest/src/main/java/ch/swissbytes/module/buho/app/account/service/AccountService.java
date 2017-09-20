package ch.swissbytes.module.buho.app.account.service;

import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.dao.VerificationTokenDao;
import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.AccountDeviceDto;
import ch.swissbytes.domain.dto.AccountDto;
import ch.swissbytes.domain.dto.DeviceDto;
import ch.swissbytes.domain.dto.UserManagementDto;
import ch.swissbytes.domain.entities.VerificationTokenEntity;
import ch.swissbytes.domain.enumerator.SellerEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.geofence.service.FenceService;
import ch.swissbytes.module.buho.app.role.repository.RoleRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.buho.app.account.exception.AccountNotFoundException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.utils.AppConfiguration;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AccountService {

    @Inject
    private Logger log;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private AccountRepository accountDao;
    @Inject
    private ConfigurationRepository configurationRepository;
    @Inject
    private VerificationTokenDao verificationTokenDao;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private GeoFenceRepository fenceRepository;
    @Inject
    private FenceService fenceService;
    @Inject
    private CoordinateDao coordinateDao;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private AppConfiguration labels;
    @Inject
    private ViewerDao viewerDao;


    public List<AccountDto> findAccountsByUserLogged(Identity identity) {


        if (identity.getIdentityDto().isBuhoAdmin()) {
            return findAllAccounts();
        }

        if (identity.getIdentityDto().isUbidataAdmin()) {
            return filterBySeller();
        }
        return Collections.emptyList();
    }

    public List<Account> ubidataAccounts() {
        return accountDao.findBySeller(SellerEnum.UBIDATA);
    }

    public List<Account> findBySeller(SellerEnum seller) {
        return accountDao.findBySeller(seller);
    }

    private List<AccountDto> filterBySeller() {
        List<UserRole> userRoles = userRoleService.findByRoleId(2L);
        List<User> usersFiltered = userRoles.stream().filter(userRole -> userRole.getUser().getAccount().getSeller() == SellerEnum.UBIDATA).map(UserRole::getUser).collect(Collectors.toList());
        return AccountDto.convertFromUserList(usersFiltered);

    }

    private List<AccountDto> findAllAccounts() {
        List<UserRole> userList = userRoleService.findByRoleId(2L);
        List<User> userEntityList = userList.stream().map(UserRole::getUser).collect(Collectors.toList());
        return AccountDto.convertFromUserList(userEntityList);
    }

    public Account getByEmail(String email) {
        Optional<Account> account = accountDao.getByEmail(email);
        if (account.isAbsent())
            throw new AccountNotFoundException();
        return account.get();
    }

    public Account createFrom(AccountDto accountDto, Identity identity) {
        User user = AccountDto.from(accountDto);
        Account account = accountDto.fromDto();
        return storeAccount(account, user, identity);
    }

    private void validateAccount(Account detachedAccount) throws InvalidInputException {
        Optional<Account> accountOptional = accountDao.getByName(detachedAccount.getName());
        if (accountOptional.isPresent()) {
            throw new InvalidInputException(labels.getMessage("msg.account.name.registered"));
        }
    }

    public Response findAllAccountWithDevices() {
        List<Account> accountList = accountDao.findAll();
        List<AccountDeviceDto> accountDeviceDtoList = accountList.stream()
                .map(account -> AccountDeviceDto.fromAccountAndDeviceList(account, DeviceDto.fromDeviceList(deviceRepository.findByAccountId(account.getId()))))
                .collect(Collectors.toList());
        return Response.ok().entity(accountDeviceDtoList).build();
    }

    public Account store(Account detachedAccount) {
        return accountDao.merge(detachedAccount);
    }

    public Account storeAccount(Account detachedAccount, User detachedUser, Identity identity) {
        Account account;

        if (detachedAccount.isNew()) {
            validateAccount(detachedAccount);
            detachedAccount.setSeller(identity.getIdentityDto().isBuhoAdmin() ? SellerEnum.SWISSBYTES : identity.getIdentityDto().isUbidataAdmin() ? SellerEnum.UBIDATA : SellerEnum.SWISSBYTES);
            account = store(detachedAccount);
            log.info(String.format("Account %s is registered!", account.getName()));

            if (detachedUser.isNew()) {
                detachedUser.setAccount(account);
                userService.validateUser(detachedUser);
                User user = userService.storeUser(detachedUser);
                setRolesToAdminAccount(user);
            }
        } else {
            account = store(detachedAccount);
            userService.storeUser(detachedUser);
        }
        return account;
    }

    public Account update(Account detachedAccount) throws EJBException {
        if (detachedAccount.getNit().length() > 20)
            throw new EJBException(labels.getMessage("msg.invalid.nit"));

        if (detachedAccount.getDocumentNumber().length() > 10)
            throw new EJBException(labels.getMessage("msg.invalid.document.number"));

        if (detachedAccount.getSocialReason().length() > 40)
            throw new EJBException(labels.getMessage("msg.invalid.social.reason"));

        return accountDao.merge(detachedAccount);
    }

    private User storeAccount(Account detachedAccount, User detachedUser, List<UserDevice> userDeviceList) throws EJBException {
        log.info(String.format("account=> %s user=> %s", detachedAccount, detachedUser));
        validateAccount(detachedAccount);
        userService.validateUser(detachedUser);
        return store(detachedAccount, detachedUser, userDeviceList);
    }

    private User store(Account detachedAccount, User detachedUser, List<UserDevice> userDeviceList) {
        Account account;
        User user = User.createNew();
        if (detachedAccount.isNew()) {
            account = accountDao.merge(detachedAccount);

            if (detachedAccount.isNew()) {
                detachedUser.setAccount(account);
                user = userService.storeUser(detachedUser, userDeviceList);
                setRolesToAdminAccount(user);
            }
        } else {
            accountDao.merge(detachedAccount);
            user = userService.storeUser(detachedUser);
        }
        return user;
    }

    public User storeAccount(UserManagementDto userManagementDto) {
        Account account = Account.createNew();
        account.setName(userManagementDto.getName());
        account.setEmail(userManagementDto.getEmail());
        User user = userManagementDto.toUserEntity();
        List<UserDevice> userDeviceList = userManagementDto.toUserDeviceList();
        return storeAccount(account, user, userDeviceList);
    }

    public User storeAccountFromWeb(UserManagementDto userManagementDto) {
        Account account = Account.createNew();
        account.setName(userManagementDto.getName());
        account.setEmail(userManagementDto.getEmail());
        User user = userManagementDto.toUserEntity();
        List<UserDevice> userDeviceList = userManagementDto.toUserDeviceList();
        return store(account, user, userDeviceList);
    }


    public void delete(Account account) {
        Long accountId = account.getId();

        List<VerificationTokenEntity> tokenEntityList = verificationTokenDao.findByCompanyId(accountId);
        List<User> userList = userRepository.findByAccountId(accountId);
        List<Device> deviceList = deviceRepository.findByAccountId(accountId);

        accountDao.delete(tokenEntityList);
        userService.delete(userList);
        fenceService.deleteByDeviceList(deviceList);

        // make are free devices.
        for (Device device : deviceList) {
            device.setAccount(null);
        }

        account.setStatus(StatusEnum.DISABLED);
        accountDao.merge(account);
        deviceRepository.saveAll(deviceList);

    }

    private void setRolesToAdminAccount(User user) {
        String rolesAdmin = KeyAppConfiguration.getString(ConfigurationKey.ROLE_ADMIN_ID);
        for (String roleId : rolesAdmin.split(";")) {
            UserRole userRole = new UserRole(user.getId(), Long.parseLong(roleId));
            userRoleService.storeUserRole(userRole);
        }
    }

    public boolean isTestAccount(Long accountId) {
        log.info("isTestAccount: " + accountId + " == " + configurationRepository.getCompanyTestId());
        return accountId == configurationRepository.getCompanyTestId();
    }
}
