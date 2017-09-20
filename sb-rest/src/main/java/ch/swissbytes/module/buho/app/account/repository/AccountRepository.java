package ch.swissbytes.module.buho.app.account.repository;

import ch.swissbytes.domain.enumerator.SellerEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AccountRepository extends Repository {

    public List<Account> findAll() {
        return findAll(Account.class);
    }

    public Optional<Account> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Account.class, id);
    }

    public Optional<Account> getByEmail(String email) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("email", email);
        return getBy(Account.class, filter);
    }

    public Optional<Account> getByName(final String name) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        return StringUtil.isEmpty(name) ? Optional.absent() : getBy(Account.class, filters);
    }

    public List<Account> findBySeller(SellerEnum seller) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("seller", seller);
        return findBy(Account.class, filter);
    }
}
