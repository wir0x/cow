package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.Alarm;
import ch.swissbytes.module.shared.persistence.Repository;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AlarmDao extends Repository {

    public List<Alarm> findAll() {
        return findAll(Alarm.class);
    }

    public List<Alarm> findPending() {
        Map<String, Object> filter = new HashMap<>();
        filter.put("isSend", false);
        return findBy(Alarm.class, filter);
    }
}
