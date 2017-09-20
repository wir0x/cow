package ch.swissbytes.domain.entities;

import ch.swissbytes.module.buho.app.permission.model.Permission;
import ch.swissbytes.module.buho.app.role.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "role_permission", uniqueConstraints = {@UniqueConstraint(name = "idx_role_permission", columnNames = {"role_id", "permission_id"})})
@Getter
@Setter
public class RolePermission {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role_permission_role_id"))
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "fk_role_permission_permission_id"))
    private Permission permission;


}