package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NewSubscriptionDto implements Serializable {

    private Long id;
    private String name;
    private String username;
    private Boolean accessViewer;
    private Boolean userPay;
    private String invitationMails;
    private String invitationNumber;
    private Boolean isInShoppingCart;

    public User userFromDto(Long accountId) {
        User user = User.createNew();
        user.setName(getName());
        user.setUsername(getUsername());
        user.setChangePassword(true);
        user.setPassword(user.generatePassword(KeyAppConfiguration.getInt(ConfigurationKey.DIGIT_NUMBER_PASSWORD)));
        user.setAccount(new Account(accountId));
        user.setEmail(null);
        return user;
    }

    public UserRole userRoleFromDto() {
        UserRole userRole = UserRole.createNew();
        userRole.setRole(new Role(KeyAppConfiguration.getLong(ConfigurationKey.ROLE_USER_ID)));
        return userRole;
    }

    public Device deviceFromDto(Long accountId) {
        Device device = Device.createNew();
        device.setName(getName());
        device.setDeviceType(new DeviceType(KeyAppConfiguration.getLong(ConfigurationKey.SMARTPHONE_TYPE_ID)));
        device.setStatus(StatusEnum.PENDING);
        device.setAccount(new Account(accountId));
        return device;
    }

    public Subscription subscriptionFromDto() {
        Subscription subscription = Subscription.createNew();
        subscription.setUserPay(getUserPay());
        subscription.setStatus(StatusEnum.DISABLED);
        return subscription;
    }
}
