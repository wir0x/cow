package ch.swissbytes.module.buho.app.geofence.service;


import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.enumerator.FenceStatusEnum;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.buho.service.IGeoFenceService;
import ch.swissbytes.module.shared.notifications.mail.template.geofence.MailSenderGeoFences;
import ch.swissbytes.module.shared.notifications.push_notification.geofence.PushNotificationFence;
import ch.swissbytes.module.shared.notifications.sms.template.geofence.SmsGeoFenceSender;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.List;

public class GeoFenceServiceImpl implements IGeoFenceService {

    @Inject
    private Logger log;

    @Inject
    private GeoFenceRepository geoFenceRepository;

    @Inject
    private FenceService fenceService;

    @Inject
    private PositionService positionService;

    @Inject
    private CoordinateDao coordinateDao;

    @Inject
    private PushNotificationFence pushNotificationFence;

    @Inject
    private SmsGeoFenceSender smsGeoFenceSender;

    @Inject
    private MailSenderGeoFences mailSenderGeoFences;


    @Override
    public void sendNotification() {
        List<GeoFence> geoFences = fenceService.findEnabledGeoFenceOfToday();
        log.info("Enable Geo Fences found: " + geoFences.size());

        for (GeoFence geoFence : geoFences) {

            if (geoFence.getPositionPointer() == null) {
                fenceService.setPositionPointer(geoFence);
            } else {
                executeValidationAndSending(geoFence);
            }
        }
    }

    private void executeValidationAndSending(GeoFence geoFence) {
        final Long deviceId = geoFence.getDevice().getId();
        final Long positionPointer = geoFence.getPositionPointer();

        log.info(String.format("Device: %s GeoFence: %s PositionPointer: %s", deviceId, geoFence.getId(), positionPointer));
        List<Position> positions = positionService.findByDeviceIdAndPositionPointerOfToday(deviceId, positionPointer);
        log.info("Positions found: {} " + positions.size());

        if (!positions.isEmpty())
            fenceService.updatePositionPointer(geoFence, positions);

        for (Position position : positions) {
            validateAndSendNotification(geoFence, position);
        }
    }

    private void validateAndSendNotification(GeoFence geoFence, Position position) {
        log.info("validateAndSendNotification... ");
        List<Position> beforeLastPositions = positionService.findBeforeLastOfToday(position.getDevice(), position.getId());

        if (beforeLastPositions.size() >= 2) {
            log.info(String.format("Position Id: %s Latitude %s Longitude: %s: ",
                    position.getId(),
                    position.getLatitude(),
                    position.getLongitude()));
            log.info(String.format("Position 1 Id: %s Latitude %s Longitude: %s ",
                    beforeLastPositions.get(0).getId(),
                    beforeLastPositions.get(0).getLatitude(),
                    beforeLastPositions.get(0).getLongitude()));
            log.info(String.format("Position 2 Id: %s Latitude %s Longitude: %s ",
                    beforeLastPositions.get(1).getId(),
                    beforeLastPositions.get(1).getLatitude(),
                    beforeLastPositions.get(1).getLongitude()));

            FenceStatusEnum fenceStatus = getStatusFence(position, beforeLastPositions, geoFence);
            log.info("Fence Enum status: " + fenceStatus.getLabel());
            log.info("Fence is Inside: " + geoFence.getIsInside());

            if (fenceStatus == FenceStatusEnum.ENTER && !geoFence.getIsInside()) {
                geoFence.setIsInside(true);
                geoFenceRepository.update(geoFence);
                sendNotifications(geoFence, fenceStatus, position);

            } else if (fenceStatus == FenceStatusEnum.LEFT) {
                geoFence.setIsInside(false);
                geoFenceRepository.update(geoFence);
                sendNotifications(geoFence, fenceStatus, position);

            } else if (fenceStatus == FenceStatusEnum.NONE) {
                geoFence.setIsInside(false);
                geoFenceRepository.update(geoFence);
            }
        }
    }

    private void sendNotifications(GeoFence geoFence, FenceStatusEnum fenceStatus, Position position) {
        pushNotificationFence.add(geoFence, fenceStatus, position);
        smsGeoFenceSender.add(geoFence, fenceStatus, position);
        mailSenderGeoFences.add(geoFence, fenceStatus, position);
    }

    private FenceStatusEnum getStatusFence(Position lastPosition, List<Position> beforeLastPositions, GeoFence geoFence) {
        FenceStatusEnum fenceStatusEnum = FenceStatusEnum.NONE;

        List<Coordinate> coordinates = null;
        if (geoFence.getCoordinateList() != null) {
            if (!geoFence.getCoordinateList().isEmpty()) {
                log.info("Geo Fence coordinate List size: " + geoFence.getCoordinateList().size());
                coordinates = geoFence.getCoordinateList();
            }
        }

        boolean isInFenceLast = findPoint(lastPosition.getLatitude(),
                lastPosition.getLongitude(),
                geoFence.getId(), coordinates);
        boolean isInFenceBeforeLast = findPoint(beforeLastPositions.get(0).getLatitude(),
                beforeLastPositions.get(0).getLongitude(),
                geoFence.getId(), coordinates);
        boolean isInFenceBeforeLastTwo = findPoint(beforeLastPositions.get(1).getLatitude(),
                beforeLastPositions.get(1).getLongitude(),
                geoFence.getId(), coordinates);

        if (isInFenceLast == isInFenceBeforeLast && isInFenceBeforeLast != isInFenceBeforeLastTwo) {
            if (isInFenceBeforeLast && geoFence.getEnteringZone())
                fenceStatusEnum = FenceStatusEnum.ENTER;

            if (isInFenceBeforeLastTwo && geoFence.getLeavingZone())
                fenceStatusEnum = FenceStatusEnum.LEFT;
        }

        return fenceStatusEnum;
    }

    private boolean findPoint(double latitude, double longitude, Long fenceId, List<Coordinate> coordinates) {
        int i, j;
        boolean pointStatus = false;
        List<Coordinate> coordinateList = coordinates != null ? coordinates : coordinateDao.findByFenceId(fenceId);

        for (i = 0, j = coordinateList.size() - 1; i < coordinateList.size(); j = i++) {
            if ((coordinateList.get(i).getLatitude() > latitude) != (coordinateList.get(j).getLatitude() > latitude) &&
                    (longitude < (coordinateList.get(j).getLongitude() - coordinateList.get(i).getLongitude()) *
                            (latitude - coordinateList.get(i).getLatitude()) / (coordinateList.get(j).getLatitude() -
                            coordinateList.get(i).getLatitude()) + coordinateList.get(i).getLongitude())) {
                pointStatus = !pointStatus;
            }
        }
        return pointStatus;
    }
}
