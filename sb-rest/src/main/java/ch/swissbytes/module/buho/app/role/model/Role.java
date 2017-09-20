package ch.swissbytes.module.buho.app.role.model;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(name = "idx_role_name", columnNames = {"name"})})
@Getter
@Setter
@ToString
public class Role {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "visible")
    private Boolean visible;

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public static Role createNew() {
        Role role = new Role();
        role.name = EntityUtil.DEFAULT_STRING;
        role.description = EntityUtil.DEFAULT_STRING;
        role.visible = EntityUtil.DEFAULT_BOOLEAN;
        return role;
    }
}
