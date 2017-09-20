package ch.swissbytes.module.buho.app.role.repository;


import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class RoleRepository extends Repository {

    public List<Role> findAll() {
        return findAll(Role.class);
    }

    public List<Role> findVisible() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("visible", true);
        return findBy(Role.class, filters);
    }

    public Optional<Role> getById(Long groupId) {
        return getById(Role.class, groupId);
    }
}
