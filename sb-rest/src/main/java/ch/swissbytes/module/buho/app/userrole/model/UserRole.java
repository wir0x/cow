package ch.swissbytes.module.buho.app.userrole.model;

import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.user.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "user_role", uniqueConstraints = {@UniqueConstraint(name = "idx_user_role", columnNames = {"user_id", "role_id"})})
@Getter
@Setter
@ToString
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_role_user_id"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_role_role_id"))
    private Role role;

    public UserRole() {

    }

    public UserRole(Long userId, Long roleId) {
        user = new User(userId);
        role = new Role(roleId);
    }

    public static UserRole createNew() {
        UserRole userRole = new UserRole();
        userRole.setUser(null);
        userRole.setRole(null);
        return userRole;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof UserRole)) return false;

        UserRole other = (UserRole) obj;
        return this.user.getId() == other.user.getId()
                && this.role.getId() == other.role.getId();
    }
}
