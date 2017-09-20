package ch.swissbytes.module.buho.app.user.repository;

import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.Encode;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class UserRepository extends Repository {

    public Optional<User> findById(Long id) {
        return getById(User.class, id);
    }

    public Optional<User> findByEmail(final String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        return getBy(User.class, params);
    }

    public Optional<User> findByUserName(String username) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("username", username);
        return getBy(User.class, filter);
    }

    public List<User> findAll() {
        return findAll(User.class);
    }

    public List<String> findPermissionsByUserId(Long userId) {
        String query = "SELECT g.permission.name " +
                "FROM RolePermission g " +
                "WHERE g.role.id IN (SELECT u.role.id FROM UserRole u WHERE u.user.id = :userId) ";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findBy(query, params);
    }

    public List<Role> findRoleByUserId(Long userId) {
        String query = "SELECT u.role " +
                "FROM UserRole u " +
                "WHERE u.user.id = :userId)";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findBy(query, params);
    }

    public List<User> findByAccountId(Long accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("account.id", accountId);
        return findBy(User.class, params);
    }

    protected String getEntity() {
        return User.class.getSimpleName();
    }

    public Optional<User> getById(final Long id) {
        return id == null || id == 0 ? Optional.ABSENT : getById(User.class, id);
    }

    public Optional<User> getByUsername(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return StringUtil.isEmpty(username) ? Optional.ABSENT : super.getBy(User.class, params);
    }

    public Optional<User> login(String username, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", Encode.encode(password));
        return StringUtil.isEmpty(username) ? Optional.ABSENT : super.getBy(User.class, params);
    }

    public Optional<User> loginEmail(String email, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", Encode.encode(password));
        return StringUtil.isEmpty(email) ? Optional.absent() : super.getBy(User.class, params);
    }

    public Optional<User> getByEmail(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        return StringUtil.isEmpty(email) ? Optional.absent() : super.getBy(User.class, params);
    }

    public Optional<User> getByIdAndPassword(Long id, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("password", Encode.encode(password));

        return LongUtil.isEmpty(id) || StringUtil.isEmpty(password) ? Optional.absent() : super.getBy(User.class, params);
    }

    public List<User> findAccountAdmin(Long accountId) {
        String query = "SELECT u.user FROM UserRole u WHERE u.user.account.id = :accountId AND u.role.id = :roleId";
        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("roleId", 2L);
        return LongUtil.isEmpty(accountId) ? Collections.emptyList() : findBy(query, params);
    }
}