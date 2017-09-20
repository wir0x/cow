package ch.swissbytes.module.buho.service;

import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.dao.PositionPointerDao;
import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.entities.PositionPointer;
import ch.swissbytes.domain.enumerator.FenceStatusEnum;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.geofence.service.FenceService;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.shared.notifications.mail.template.geofence.MailSenderGeoFences;
import ch.swissbytes.module.shared.notifications.push_notification.geofence.PushNotificationFence;
import ch.swissbytes.module.shared.notifications.sms.template.geofence.SmsGeoFenceSender;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.DateUtil;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Stateless
public class AlarmsFencesService {

    @Inject
    private Logger log;

    @Inject
    private FenceService fenceService;

    @Inject
    private CoordinateDao coordinateDao;

    @Inject
    private PositionService positionService;

    @Inject
    private SmsGeoFenceSender smsGeoFenceSender;

    @Inject
    private MailSenderGeoFences mailSenderGeoFences;

    @Inject
    private PushNotificationFence pushNotificationFence;

    @Inject
    private PositionPointerService positionPointerService;

    @Inject
    private PositionRepository positionRepository;

    @Inject
    private GeoFenceRepository fenceRepository;

    @Inject
    private PositionPointerDao positionPointerDao;

    public void findAndSendNotifications() {
        List<GeoFence> geoFences = fenceService.findEnabledGeoFenceOfToday();
        log.info("GeoFences enabled: " + geoFences.size());
        for (GeoFence geoFence : geoFences) {

            if (geoFence.getPositionPointer() == null) {
                log.info("position pointer fence NULL...");
                fenceService.setPositionPointer(geoFence);
            } else {
                executeValidationAndSending(geoFence);
            }
        }
    }

    private void executeValidationAndSending(GeoFence geoFence) {
        final Long deviceId = geoFence.getDevice().getId();
        final Long positionPointer = geoFence.getPositionPointer();
        List<Position> positions = positionService.findByDeviceIdAndPositionPointerOfToday(deviceId, positionPointer);
        log.info(String.format("Device: %s GeoFence: %s PositionPointer: %s", deviceId, geoFence.getDevice(), positionPointer));
        log.info("Found positions: " + positions.size());
        for (Position position : positions) {
            validateAndSendNotification(geoFence, position);
        }
        fenceService.updatePositionPointer(geoFence, positions);
    }

    private void validateAndSendNotification(GeoFence geoFence, Position position) {
        List<Position> beforeLastPositions = positionService.findBeforeLastOfToday(position.getDevice(), position.getId());
        log.info("beforeLastPositions: " + beforeLastPositions.size());

        if (beforeLastPositions.size() > 2) {
            FenceStatusEnum fenceStatus = getStatusFence(position, beforeLastPositions, geoFence);

            log.info("Status geo fence: " + fenceStatus.getLabel());

            if (FenceStatusEnum.NONE != fenceStatus) {
                sendNotifications(geoFence, fenceStatus, position);
            }
        }
    }

    private void sendNotifications(GeoFence geoFence, FenceStatusEnum fenceStatus, Position position) {
        pushNotificationFence.add(geoFence, fenceStatus, position);
        smsGeoFenceSender.add(geoFence, fenceStatus, position);
        mailSenderGeoFences.add(geoFence, fenceStatus, position);
    }

    private boolean findPoint(double latitude, double longitude, Long fenceId, List<Coordinate> coordinates) {
        int i, j;
        boolean pointStatus = false;
        List<Coordinate> coordinateList;
        if (coordinates != null) {
            coordinateList = coordinates;
        } else {
            coordinateList = coordinateDao.findByFenceId(fenceId);
        }

        for (i = 0, j = coordinateList.size() - 1; i < coordinateList.size(); j = i++) {
            if ((coordinateList.get(i).getLatitude() > latitude) != (coordinateList.get(j).getLatitude() > latitude) &&
                    (longitude < (coordinateList.get(j).getLongitude() - coordinateList.get(i).getLongitude()) * (latitude - coordinateList.get(i).getLatitude()) / (coordinateList.get(j).getLatitude() - coordinateList.get(i).getLatitude()) + coordinateList.get(i).getLongitude())) {
                pointStatus = !pointStatus;
            }
        }
        return pointStatus;
    }

    private FenceStatusEnum getStatusFence(Position lastPosition, List<Position> beforeLastPositions, GeoFence geoFence) {

        List<Coordinate> coordinates = null;
        if (geoFence.getCoordinateList() != null) {

            if (geoFence.getCoordinateList().isEmpty()) {
                log.info("is empty null");
            } else {
                log.info("size: " + geoFence.getCoordinateList().size());
                coordinates = geoFence.getCoordinateList();
            }
        } else {
            log.info("coordinate is null ");
        }

        boolean isInFenceLast = findPoint(lastPosition.getLatitude(), lastPosition.getLongitude(), geoFence.getId(), coordinates);
        boolean isInFenceBeforeLast = findPoint(beforeLastPositions.get(0).getLatitude(), beforeLastPositions.get(0).getLongitude(), geoFence.getId(), coordinates);
        boolean isInFenceBeforeLastTwo = findPoint(beforeLastPositions.get(1).getLatitude(), beforeLastPositions.get(1).getLongitude(), geoFence.getId(), coordinates);

        log.info("beforeLastPosition: " + lastPosition.getId() + " latitude: " + lastPosition.getLatitude() + " longitude: " + lastPosition.getLongitude() + "isInFenceLast: " + isInFenceLast);
        log.info("beforeLastPosition: " + beforeLastPositions.get(0).getId() + " latitude: " + beforeLastPositions.get(0).getLatitude() + " longitude: " + beforeLastPositions.get(0).getLongitude() + "isInFenceBeforeLast: " + isInFenceBeforeLast);
        log.info("beforeLastPosition: " + beforeLastPositions.get(1).getId() + " latitude: " + beforeLastPositions.get(1).getLatitude() + " longitude: " + beforeLastPositions.get(1).getLongitude() + "isInFenceBeforeLastTwo: " + isInFenceBeforeLastTwo);

        if (isInFenceLast == isInFenceBeforeLast && isInFenceBeforeLast != isInFenceBeforeLastTwo) {
            if (isInFenceBeforeLast && geoFence.getEnteringZone())
                return FenceStatusEnum.ENTER;
            if (isInFenceBeforeLastTwo && geoFence.getLeavingZone())
                return FenceStatusEnum.LEFT;
        }
        return FenceStatusEnum.NONE;
    }

    public void findFencesAndMakeNotification() {
        Optional<PositionPointer> positionPointer = positionPointerDao.getLastFencePointerPosition();
        if (positionPointer.isPresent()) {
            log.info("getLastFencePointerPosition: " + positionPointer.get());

            List<Position> positionList = positionRepository.findByPositionPointer(positionPointer.get().getPointer());
            log.info("positionList: " + positionList.size());

            for (Position position : positionList) {
                positionPointer.get().setPointer(position.getId());
                positionPointerService.update(positionPointer.get());
                verifyFenceListByPosition(position);
            }
        }
    }


    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void verifyFenceListByPosition(Position position) {
        log.info("verifyFenceList... started. ");
        long start = System.currentTimeMillis();


        List<GeoFence> geoFenceValidList = findValidFencesByDeviceId(position.getDevice().getId());

        if (!(geoFenceValidList.isEmpty())) {
            List<Position> beforeLastPositions = positionRepository.getBeforeLast(position.getId(), position.getDevice().getId());

            if (beforeLastPositions.size() >= 2) {
                for (GeoFence geoFence : geoFenceValidList) {
                    FenceStatusEnum fenceStatus = getStatusFence(position, beforeLastPositions, geoFence);
                    if (FenceStatusEnum.NONE != fenceStatus) {
                        pushNotificationFence.add(geoFence, fenceStatus, position);
                        smsGeoFenceSender.add(geoFence, fenceStatus, position);
                        mailSenderGeoFences.add(geoFence, fenceStatus, position);
                    }
                }
            }
        }
        log.info("verifyFenceList... finished: " + (System.currentTimeMillis() - start));
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    private List<GeoFence> findValidFencesByDeviceId(Long deviceId) {
        Calendar calendar = Calendar.getInstance();
        String weekDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).toLowerCase();
        List<GeoFence> geoFenceList = fenceRepository.findEnableFences(weekDay, deviceId);
        geoFenceList = geoFenceList.isEmpty() ? new ArrayList<>() : geoFenceList.stream().filter(fence -> (DateUtil.isTimeAfterNow(fence.getStartTime()) && DateUtil.isTimeBeforeNow(fence.getEndTime()))).collect(Collectors.toList());
        return geoFenceList;
    }
}
