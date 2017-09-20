package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.VerificationTokenDao;
import ch.swissbytes.domain.entities.VerificationTokenEntity;
import ch.swissbytes.module.buho.app.user.model.User;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class VerificationTokenService {

    @Inject
    private Logger log;

    @Inject
    private VerificationTokenDao tokenDao;


    public VerificationTokenEntity getVerificationTokenGenerated(User user) {
        return tokenDao.generateVerificationToken(user);
    }

    /**
     * Returns the VerificationTokenEntity is has active Token otherwise returns Null
     *
     * @param token
     * @return
     */
    public VerificationTokenEntity getActiveVerificationToken(String token) {
        log.info("getActiveVerificationToken(String token[" + token + "])");
        List<VerificationTokenEntity> list = tokenDao.getActiveVerifyToken(token);
        VerificationTokenEntity entity = null;
        if (!list.isEmpty()) {
            entity = list.get(0);
            if (entity.isVerified() || entity.getExpirationDate().before(new Date())) {
                return null;
            }
        }
        return entity;
    }

    public void updateToken(VerificationTokenEntity detachedTokenEntity) {
        tokenDao.updateToken(detachedTokenEntity);
    }

    private boolean hasExpired(VerificationTokenEntity entity) {
        Date tokenDate = new Date();
        return tokenDate.after(entity.getExpirationDate());
    }

}
