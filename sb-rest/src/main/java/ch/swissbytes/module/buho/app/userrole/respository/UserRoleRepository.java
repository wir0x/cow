package ch.swissbytes.module.buho.app.userrole.respository;

import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class UserRoleRepository extends Repository {

    public Optional<UserRole> findById(Long id) {
        return getById(UserRole.class, id);
    }

    public List<UserRole> findByUserId(Long userId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return findBy(UserRole.class, filter);
    }

    public Optional<UserRole> getByUserId(Long userId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return getBy(UserRole.class, filter);
    }

    public List<UserRole> findByRoleId(Long roleId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("role.id", roleId);
        return findBy(UserRole.class, filter);
    }
}
