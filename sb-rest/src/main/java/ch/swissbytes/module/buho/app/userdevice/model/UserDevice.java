package ch.swissbytes.module.buho.app.userdevice.model;

import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "user_device", uniqueConstraints = {@UniqueConstraint(name = "idx_user_device", columnNames = {"user_id", "device_id"})})
@Getter
@Setter
@ToString
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_device_user_id"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "fk_user_device_device_id"))
    private Device device;

    public UserDevice(Long id) {
        this.id = id;
    }

    public UserDevice(User user, Device device) {
        this.user = user;
        this.device = device;
    }

    public static UserDevice createNew(Long userId, Long deviceId) {
        UserDevice userDevice = new UserDevice();
        userDevice.setUser(new User(userId));
        userDevice.setDevice(new Device(deviceId));
        return userDevice;
    }

    public UserDevice() {
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof UserDevice)) return false;

        UserDevice other = (UserDevice) obj;
        return this.user.getId() == other.user.getId()
                && this.device.getId() == other.device.getId();
    }

    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }
}
