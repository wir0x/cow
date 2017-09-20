package ch.swissbytes.module.buho.app.userrole.service;

import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.app.userrole.respository.UserRoleRepository;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.exception.UserRoleNotFoundException;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class UserRoleService {

    @Inject
    private Logger log;
    @Inject
    private UserRoleRepository userRoleRepository;


    public UserRole add(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public void update(UserRole userRole) {
        validateFields(userRole);
        userRoleRepository.merge(userRole);
    }

    public UserRole storeUserRole(UserRole detachedUserRole) {
        return userRoleRepository.save(detachedUserRole);
    }

    public void removeUserRole(final UserRole userRole) {
        userRoleRepository.remove(userRole);
    }

    public void removeUserRole(Long userId) {
        Optional<UserRole> userRoleOptional = userRoleRepository.getByUserId(userId);
        if (userRoleOptional.isPresent())
            removeUserRole(userRoleOptional.get());
    }

    public void updateRolUser(final UserRole userRole) {
        Optional<UserRole> userRoleOpt = userRoleRepository.getByUserId(userRole.getUser().getId());
        if (userRoleOpt.isAbsent()) {
            storeUserRole(userRole);
        }
    }

    public boolean userIsSystemAdmin(User user) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        long idSystemAdmin = KeyAppConfiguration.getLong(ConfigurationKey.SYSTEM_ADMIN_GROUP_ID);

        for (UserRole userRole : userRoles) {
            if (userRole.getRole().getId() == idSystemAdmin)
                return true;
        }
        return false;
    }

    public boolean userIsBuhoSystemAdmin(User user) {
        if (!userIsSystemAdmin(user))
            return false;

        final String username = KeyAppConfiguration.getString(ConfigurationKey.USERNAME_BUHO_SYS_ADMIN);
        return user.getUsername().equals(username);
    }

    public boolean userIsUbidataAdmin(User user) {
        if (!userIsSystemAdmin(user))
            return false;

        final String username = KeyAppConfiguration.getString(ConfigurationKey.USERNAME_UBIDATA_SYS_ADMIN);
        return user.getUsername().equals(username);
    }

    public List<UserRole> findByRoleId(Long id) {
        return userRoleRepository.findByRoleId(id);
    }


    private void validateFields(UserRole userRole) {
        if (userRole == null)
            throw new UserRoleNotFoundException();

        final Optional<UserRole> userRoleOptional = userRoleRepository.findById(userRole.getId());

        if (userRoleOptional.isAbsent())
            throw new UserRoleNotFoundException();
    }
}
