package ch.swissbytes.module.buho.app.position.repository;

import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.*;

@Stateless
public class PositionRepository extends Repository {

    public Optional<Position> getById(Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Position.class, id);
    }

    public Optional<Position> getLastByDeviceId(Long deviceId) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT p FROM ");
        jpql.append("Position p ");
        jpql.append("WHERE p.device.id = :deviceId ");
        jpql.append("AND p.valid = true ");
        jpql.append("ORDER BY p.time DESC ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("deviceId", deviceId);
        return getBy(jpql.toString(), filters);
    }

    public Optional<Position> getLastByDeviceIdAndDates(Long deviceId, Date fromDate, Date toDate) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT p FROM ");
        jpql.append("Position p ");
        jpql.append("WHERE p.device.id = :deviceId ");
        jpql.append("AND p.time BETWEEN :fromDate AND :toDate ");
        jpql.append("AND p.valid = true ");
        jpql.append("ORDER BY p.time DESC ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("deviceId", deviceId);
        filters.put("fromDate", fromDate);
        filters.put("toDate", toDate);
        return getBy(jpql.toString(), filters);
    }

    public List<Position> findAll() {
        return findAll(Position.class);
    }

    public List<Position> findByDeviceIdAndDate(Long id, Date dateInit, Date dateEnd) {
        Query query = entityManager.createQuery("SELECT p "
                + "FROM Position p "
                + "WHERE p.device.id = :id "
                + "AND p.time BETWEEN :dateInit AND :dateEnd "
                + "AND p.valid = true "
                + "GROUP BY p.latitude, p.longitude "
                + "ORDER BY p.time ");
        query.setParameter("id", id);
        query.setParameter("dateInit", dateInit);
        query.setParameter("dateEnd", dateEnd);
        return query.getResultList();
    }

    public List<Position> findBy_DeviceId_Dates_and_PositionId(Long deviceId, Date fromDate, Date toDate, Long positionId) {
        Query query = entityManager.createQuery("SELECT p "
                + "FROM Position p "
                + "WHERE p.device.id = :deviceId "
                + "AND p.time BETWEEN :fromDate AND :toDate "
                + "AND p.id > :positionId "
                + "AND p.valid = :valid "
                + "GROUP BY p.latitude, p.longitude ");
        query.setParameter("deviceId", deviceId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("positionId", positionId);
        query.setParameter("valid", true);
        return query.getResultList();
    }

    public List<Position> findBeforeLast_by_DeviceId_Dates_and_PositionId(Long deviceId, Date fromDate, Date toDate, Long positionId) {
        Query query = entityManager.createQuery("SELECT p "
                + "FROM Position p "
                + "WHERE p.device.id = :deviceId "
                + "AND p.time BETWEEN :fromDate AND :toDate "
                + "AND p.valid = true "
                + "AND p.id < :positionId "
                + "GROUP BY p.latitude, p.longitude "
                + "ORDER BY p.id DESC ").setMaxResults(2);
        query.setParameter("deviceId", deviceId);
        query.setParameter("positionId", positionId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        return query.getResultList();
    }

    public List<Position> findByDeviceId(Long deviceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("device.id", deviceId);
        return LongUtil.isEmpty(deviceId) ? Collections.emptyList() : findBy(Position.class, filter);
    }

    public Boolean hasPositionInDates(Long id, Date fromDate, Date toDate) {
        Query query = entityManager.createQuery("SELECT p "
                + "FROM Position p "
                + "WHERE p.device.id = :id "
                + "AND p.time BETWEEN :fromDate AND :toDate "
                + "AND p.valid = true "
                + "ORDER BY p.time ");
        query.setParameter("id", id);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        return (!query.getResultList().isEmpty()) && (query.getResultList().size() > 2);
    }

    public void deletePositionsByDeviceId(Long deviceId) {
        Query query = entityManager.createQuery("" +
                "DELETE  " +
                "FROM Position " +
                "WHERE device.id = :deviceId ");
        query.setParameter("deviceId", deviceId);
        query.executeUpdate();
    }

    public List<Position> findByPositionPointer(Long pointer) {
        Query query = entityManager.createQuery("" +
                "SELECT p " +
                "FROM Position p " +
                "WHERE p.id > :positionId " +
                "AND p.valid = true ");
        query.setParameter("positionId", pointer);

        return query.getResultList();
    }

    public List<Position> getBeforeLast(Long positionId, Long deviceId) {
        Query query = entityManager.createQuery("" +
                "SELECT p FROM Position p " +
                "WHERE p.id < :positionId " +
                "AND p.device.id = :deviceId " +
                "ORDER BY p.id DESC ").setMaxResults(2);
        query.setParameter("positionId", positionId);
        query.setParameter("deviceId", deviceId);
        return query.getResultList();
    }
}
