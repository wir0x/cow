package ch.swissbytes.module.buho.app.notification.repository;

import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.notification.model.Notification;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class NotificationRepository extends Repository {

    public Optional<Notification> findById(Long id) {
        return getById(Notification.class, id);
    }

    public List<Notification> findWithPendingStatus() {
        Query query = entityManager.createQuery("SELECT p "
                + "FROM Notification p "
                + "WHERE p.status = :status ");
        query.setParameter("status", PaymentStatusEnum.PENDING);
        return query.getResultList();
    }
}
