package ch.swissbytes.module.buho.app.geofence.service;

import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.buho.service.CoordinateService;
import ch.swissbytes.module.shared.exception.DuplicateException;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


@Stateless
public class FenceService {

    @Inject
    private Logger log;

    @Inject
    private GeoFenceRepository fenceRepository;

    @Inject
    private CoordinateDao coordinateDao;

    @Inject
    private CoordinateService coordinateService;

    @Inject
    private PositionService positionService;


    public List<GeoFence> findEnabledGeoFenceOfToday() {
        Calendar calendar = Calendar.getInstance();
        String weekDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).toLowerCase();
        return fenceRepository.findEnableFences(weekDay);
    }

    public void updatePositionPointer(GeoFence geoFence, List<Position> positions) {
        geoFence.setPositionPointer(positions.get(positions.size() - 1).getId());
        fenceRepository.update(geoFence);
    }

    public void setPositionPointer(GeoFence geoFence) {
        Optional<Position> positionOptional = positionService.getLastOfTodayByDeviceId(geoFence.getDevice().getId());
        if (positionOptional.isPresent()) {
            geoFence.setPositionPointer(positionOptional.get().getId());
            fenceRepository.update(geoFence);
            log.info(String.format("Device: %s GeoFence: %s PositionPointer: %s UPDATED!", geoFence.getDevice().getId(), geoFence.getId(), geoFence.getPositionPointer()));
        }
    }

    public GeoFence storeFence(GeoFence detachedGeoFence, List<Coordinate> coordinateList) throws RuntimeException {
        try {
            validateFence(detachedGeoFence);
            if (detachedGeoFence.isNew()) {
                detachedGeoFence = fenceRepository.saveAndFlush(detachedGeoFence);

            } else {
                detachedGeoFence = fenceRepository.merge(detachedGeoFence);
                removeCoordinates(detachedGeoFence.getId());
            }

            if (coordinateList != null) {
                for (Coordinate coordinate : coordinateList) {
                    coordinate.setLatitude(coordinate.getLatitude());
                    coordinate.setLongitude(coordinate.getLongitude());
                    coordinate.setGeoFence(detachedGeoFence);
                    coordinateDao.merge(coordinate);
                }
            }

            return detachedGeoFence;

        } catch (PersistenceException pEx) {
            log.error(pEx.getMessage(), pEx);
            throw new DuplicateException(pEx);
        }
    }

    public String storeFence(List<GeoFence> detachedGeoFenceList) {
        log.info("storeFence...");
        String resultList = "";
        for (GeoFence detachedGeoFence : detachedGeoFenceList) {
            String result = "";
            try {

                Optional<GeoFence> fenceOptional = fenceRepository.getById(detachedGeoFence.getId());

                if (fenceOptional.isAbsent()) {
                    throw new InvalidInputException("no exist fence");
                }

                detachedGeoFence = storeFence(detachedGeoFence, detachedGeoFence.getCoordinateList());

            } catch (EJBException ejbEx) {
                InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
                log.error(ejbEx.getMessage(), ejbEx);
                result = detachedGeoFence.getName() + " " + invalidInputException.getMessage();
                resultList = resultList + "\n";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result = detachedGeoFence.getName() + " " + e.getMessage();
                resultList = resultList + "\n";
            }
        }
        return resultList;
    }

    public void deleteByDeviceList(List<Device> deviceList) {
        List<GeoFence> geoFenceList = fenceRepository.findFencesByDeviceList(deviceList);
        delete(geoFenceList);
    }

    public void delete(List<GeoFence> geoFenceList) {
        geoFenceList.forEach(this::remove);
    }

    public void remove(GeoFence geoFence) {
        // Delete coordinate of fence.
        coordinateService.delete(geoFence.getId());
        fenceRepository.remove(geoFence);
    }

    private void removeCoordinates(Long fenceId) {
        List<Coordinate> coordinateList = coordinateDao.findByFenceId(fenceId);
        coordinateList.forEach(coordinateDao::remove);
    }

    private void validateFence(GeoFence detachedGeoFence) throws InvalidInputException {
//        Optional<Fence> userEntityOptional = fenceDao.getByUsername(detachedFence.getUserName());
//        if (userEntityOptional.isPresent() && userEntityOptional.get().getId() != detachedFence.getId()) {
//            throw new InvalidInputException("username used");
//        }
//
//        userEntityOptional = fenceDao.getByEmail(detachedFence.getEmail());
//        if (userEntityOptional.isPresent() && userEntityOptional.get().getId() != detachedFence.getId()) {
//            throw new InvalidInputException("email used");
//        }
    }

}
