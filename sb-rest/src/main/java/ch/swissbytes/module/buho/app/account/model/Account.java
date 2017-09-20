package ch.swissbytes.module.buho.app.account.model;

import ch.swissbytes.domain.enumerator.SellerEnum;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "account", uniqueConstraints = {@UniqueConstraint(name = "idx_account_name", columnNames = {"name"})})
@Getter
@Setter
@ToString
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Size(min = 1, message = "El nombre de la cuenta es requerido")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "email")
    @Size(min = 1, max = 150, message = "El correo eléctronico es requerido")
    private String email;

    @Column(name = "line")
    private Integer line;

    @Column(name = "document_number")
    @Size(max = 10, message = "El numero de documento no puede tener mas de 10 digitos")
    private String documentNumber;

    @Column(name = "social_reason")
    @Size(max = 40, message = "La razon social no puede tener mas de 40 caracteres")
    private String socialReason;

    @Column(name = "nit")
    @Size(max = 20, message = "El nit no puede tener mas de 20 carateres")
    private String nit;

    @Column(name = "seller")
    @Enumerated(EnumType.STRING)
    private SellerEnum seller;

    @Transient
    private Boolean systemAdmin;

    public Account() {
    }

    public Account(Long id) {
        this.id = id;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    @Transient
    public static Account createNew() {
        Account account = new Account();
        account.id = EntityUtil.DEFAULT_LONG;
        account.name = EntityUtil.DEFAULT_STRING;
        account.address = EntityUtil.DEFAULT_STRING;
        account.phoneNumber = EntityUtil.DEFAULT_STRING;
        account.status = StatusEnum.ENABLED;
        account.email = EntityUtil.DEFAULT_STRING;
        account.line = EntityUtil.DEFAULT_INTEGER;
        account.documentNumber = EntityUtil.DEFAULT_STRING;
        account.nit = EntityUtil.DEFAULT_STRING;
        account.socialReason = EntityUtil.DEFAULT_STRING;
        account.systemAdmin = EntityUtil.DEFAULT_BOOLEAN_TRUE;

        return account;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Account)) return false;

        Account other = (Account) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.intValue();
    }
}
