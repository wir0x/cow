package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.entities.Coordinate;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class CoordinateService {

    @Inject
    private Logger log;

    @Inject
    private CoordinateDao coordinateDao;

    public void delete(Long fenceId) {
        log.info("delete coordinate by fence: " + fenceId);
        List<Coordinate> coordinateList = coordinateDao.findByFenceId(fenceId);
        coordinateList.forEach(coordinateDao::remove);
    }
}
