package ch.swissbytes.module.demo.repository;

import ch.swissbytes.module.demo.model.DemoPosition;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Stateless
public class DemoPositionDao extends Repository {

    public List<DemoPosition> findByDeviceIdAndDate(List<Long> deviceIdList, LocalDateTime dateInit, LocalDateTime dateEnd) {
        StringBuilder jpql = new StringBuilder();

        jpql.append("SELECT p ");
        jpql.append("FROM DemoPosition p ");
        jpql.append("WHERE p.deviceId IN :id ");
        jpql.append("AND p.time BETWEEN :dateInit AND :dateEnd ");
        jpql.append("ORDER BY p.time ");

        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("id", deviceIdList);
        Date startDate = Date.from(dateInit.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant());
        query.setParameter("dateInit", startDate);
        query.setParameter("dateEnd", endDate);

        List<DemoPosition> listPosition = query.getResultList();
        return listPosition;
    }
}
