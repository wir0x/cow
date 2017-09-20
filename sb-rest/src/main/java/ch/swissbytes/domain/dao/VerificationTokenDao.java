package ch.swissbytes.domain.dao;


import ch.swissbytes.domain.entities.VerificationTokenEntity;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.*;

@Stateless
public class VerificationTokenDao extends Repository {

    private static final int DEFAULT_EXPIRY_TIME_IN_HOURS = 24;

    @Transactional
    public VerificationTokenEntity generateVerificationToken(User user) {
        VerificationTokenEntity token = new VerificationTokenEntity();
        createEntityToPersist(token, user);
        super.saveAndFlush(token);
        return token;
    }

    private void createEntityToPersist(VerificationTokenEntity entity, User user) {
        entity.setToken(UUID.randomUUID().toString());
        entity.setVerified(false);
        entity.setExpirationDate(calculateExpirationDate(DEFAULT_EXPIRY_TIME_IN_HOURS));
        entity.setUser(user);
    }

    private Date calculateExpirationDate(int expirationTimeInHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, expirationTimeInHours);
        return cal.getTime();
    }

    @Transactional
    public void updateToken(VerificationTokenEntity tokenEntity) {
        VerificationTokenEntity entity = super.merge(tokenEntity);
        super.saveAndFlush(entity);
    }

    @Transactional
    public List<VerificationTokenEntity> getActiveVerifyToken(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t ");
        sb.append(" FROM VerificationTokenEntity t");
        sb.append(" LEFT JOIN t.user u ");
        sb.append(" WHERE t.token= :TOKEN");
        sb.append(" AND (u.status =:ACTIVE ");
        sb.append(" OR u.status =:INACTIVE )");
        Map<String, Object> params = new HashMap<>();
        params.put("TOKEN", token);
        params.put("ACTIVE", StatusEnum.ENABLED);
        params.put("INACTIVE", StatusEnum.DISABLED);
        return super.findBy(sb.toString(), params);

    }

    public List<VerificationTokenEntity> findByUserId(Long userId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("user.id", userId);
        return findBy(VerificationTokenEntity.class, filter);
    }

    public List<VerificationTokenEntity> findByCompanyId(Long accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user.account.id", accountId);
        return super.findBy(VerificationTokenEntity.class, params);
    }
}
