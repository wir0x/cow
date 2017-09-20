package ch.swissbytes.module.buho.app.user.model;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100)
    @Size(min = 3, message = "El nombre debe tener mas de 3 caracteres y es requerido")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "user_name", length = 50)
    @Size(min = 3, max = 50, message = "El nombre de usuario es requerido y debe tener un maximo de 50 caracteres")
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    @Size(min = 4, message = "La contraseña es requerida")
    private String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "is_without_expiration")
    private Boolean isWithoutExpiration;

    @Column(name = "change_password")
    private Boolean changePassword;

    @Column(name = "generated_password")
    private String generatedPassword;

    @ManyToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_user_account_id"))
    private Account account;

    @Transient
    private List<UserDevice> userDeviceList;

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }


    @Transient
    public static User createNew() {
        User user = new User();
        user.username = EntityUtil.DEFAULT_STRING;
        user.password = EntityUtil.DEFAULT_STRING;
        user.name = EntityUtil.DEFAULT_STRING;
        user.email = EntityUtil.DEFAULT_STRING;
        user.status = StatusEnum.ENABLED;
        user.isWithoutExpiration = EntityUtil.DEFAULT_BOOLEAN;
        user.changePassword = EntityUtil.DEFAULT_BOOLEAN;
        user.generatedPassword = EntityUtil.DEFAULT_STRING;
        user.account = null;
        return user;
    }

    public String generatePassword(int numberDigit) {
        SecureRandom random = new SecureRandom();
        String password = "";
        for (int i = 0; i < numberDigit; i++) {
            password += new BigInteger(5, random).toString(32).toUpperCase();
        }
        return password;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }
}
