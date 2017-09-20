package ch.swissbytes.domain.dao;

import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.persistence.Repository;
import ch.swissbytes.module.shared.utils.LongUtil;

import javax.ejb.Stateless;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CoordinateDao extends Repository {

    public List<Coordinate> findAll() {
        return findAll(Coordinate.class);
    }

    public Optional<Coordinate> getById(final Long id) {
        return LongUtil.isEmpty(id) ? Optional.absent() : getById(Coordinate.class, id);
    }

    public List<Coordinate> findByFenceId(Long fenceId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("geoFence.id", fenceId);
        return findBy(Coordinate.class, filter);
    }

    public List<Coordinate> findByFenceList(List<GeoFence> geoFenceList) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("fence", geoFenceList);
        return geoFenceList.isEmpty() ? Collections.emptyList() : findBy(Coordinate.class, filter);
    }

}
