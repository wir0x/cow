package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserManagementDto implements Serializable {

    private Long id;
    @Size(max = 50, message = "{user.name.size}")
    private String name;
    private String email;
    @Size(max = 100, message = "{user.username.size}")
    private String username;
    private String imei;
    private String password;
    private String passwordNew;
    private String passwordConfirm;
    private Long roleId;
    private Long[] devices;
    private String token;
    private Boolean accountAdmin;
    private Boolean systemAdmin;
    private String phoneNumber;

    public static UserManagementDto createNew() {
        UserManagementDto dto = new UserManagementDto();
        dto.id = EntityUtil.DEFAULT_LONG;
        dto.imei = EntityUtil.DEFAULT_STRING;
        dto.name = EntityUtil.DEFAULT_STRING;
        dto.email = EntityUtil.DEFAULT_STRING;
        dto.username = EntityUtil.DEFAULT_STRING;
        dto.password = EntityUtil.DEFAULT_STRING;
        dto.passwordNew = EntityUtil.DEFAULT_STRING;
        dto.passwordConfirm = EntityUtil.DEFAULT_STRING;
        dto.token = EntityUtil.DEFAULT_STRING;
        dto.accountAdmin = EntityUtil.DEFAULT_BOOLEAN;
        dto.systemAdmin = EntityUtil.DEFAULT_BOOLEAN;
        dto.phoneNumber = EntityUtil.DEFAULT_STRING;
        return dto;
    }


    public static UserManagementDto fromUserEntity(User user) {
        UserManagementDto dto = createNew();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.username = user.getUsername();
        dto.password = user.getGeneratedPassword();
        dto.phoneNumber = user.getPhoneNumber();
        return dto;
    }

    public User toUserEntity() {
        User user = User.createNew();
        user.setId(getId());
        user.setUsername(getUsername());
        user.setPassword(getPassword());
        user.setName(getName());
        user.setPhoneNumber(getPhoneNumber() == null || getPhoneNumber().isEmpty() ? "" : getPhoneNumber());
        user.setEmail(getEmail() == null || getEmail().isEmpty() ? "" : getEmail());
        user.setUserDeviceList(toUserDeviceList());
        return user;
    }

    public static UserManagementDto fromUserEntity(User user, List<UserDevice> userDevices) {
        UserManagementDto userManagementDto = fromUserEntity(user);
        userManagementDto.devices = new Long[userDevices.size()];

        for (int i = 0; i < userDevices.size(); i++) {
            userManagementDto.devices[i] = userDevices.get(i).getDevice().getId();
        }
        return userManagementDto;
    }

    public List<UserDevice> toUserDeviceList() {
        List<UserDevice> userDeviceList = new ArrayList<>();
        if (getDevices() == null) {
            return userDeviceList;
        }

        for (Long deviceId : getDevices())
            userDeviceList.add(UserDevice.createNew(getId(), deviceId));

        return userDeviceList;
    }
}