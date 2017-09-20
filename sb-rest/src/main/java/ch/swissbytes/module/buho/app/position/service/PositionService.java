package ch.swissbytes.module.buho.app.position.service;


import ch.swissbytes.domain.dto.HistoricalDto;
import ch.swissbytes.domain.dto.PositionDto;
import ch.swissbytes.domain.dto.ReportDetailedDto;
import ch.swissbytes.domain.dto.TravelDto;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.service.SubscriptionService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.DateUtil;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Pos;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Stateless
public class PositionService {

    @Inject
    private Logger log;

    @Inject
    private PositionRepository positionRepository;

    @Inject
    private UserDeviceRepository userDeviceRepository;

    @Inject
    private SubscriptionService subscriptionService;


    public Optional<Position> getLastOfTodayByDeviceId(Long deviceId) {
        Date fromDate = DateUtil.setZeroHour(new Date());
        Date toDate = DateUtil.set24Hour(new Date());
        return positionRepository.getLastByDeviceIdAndDates(deviceId, fromDate, toDate);
    }

    public List<Position> findByDeviceIdAndPositionPointerOfToday(Long deviceId, Long positionPointer) {
        Date fromDate = DateUtil.setZeroHour(new Date());
        Date toDate = DateUtil.set24Hour(new Date());
        return positionRepository.findBy_DeviceId_Dates_and_PositionId(deviceId, fromDate, toDate, positionPointer);
    }

    public List<Position> findBeforeLastOfToday(Device device, Long positionId) {
        Date fromDate = DateUtil.setZeroHour(new Date());
        Date toDate = DateUtil.set24Hour(new Date());
        return positionRepository.findBeforeLast_by_DeviceId_Dates_and_PositionId(device.getId(), fromDate, toDate, positionId);
    }

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public List<Position> getByDeviceIdAndDate(Long id, String strDate) {
        List<Position> positions = new ArrayList<>();
        Date date = DateUtil.getDateFromString(strDate);
        Date fromDate = DateUtil.setZeroHour(date);
        Date toDate = DateUtil.set24Hour(date);

        if (positionRepository.hasPositionInDates(id, fromDate, toDate)) {
            positions = positionRepository.findByDeviceIdAndDate(id, fromDate, toDate);
            log.info("size distinct: " + positions.size());
        }
        return positions;

    }

    public List<PositionDto> lastOfDevicesByUserId(Long id) {
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(id);
        return currentPositions(userDevices);
    }

    private List<PositionDto> currentPositions(List<UserDevice> userDevices) {
        List<PositionDto> positionDtos = new ArrayList<>();
        int i = 0;
        for (UserDevice userDevice : userDevices) {
            final Optional<Position> position = positionRepository.getLastByDeviceId(userDevice.getDevice().getId());
            final Subscription subscription = subscriptionService.getByDeviceId(userDevice.getDevice().getId());

            if (position.isPresent())
                positionDtos.add(PositionDto.from(position.get(), subscription, i++));
        }
        return positionDtos;
    }

    private List<TravelDto> travelDtoList(List<Position> positionList) {
        log.info(String.format("positions: %s ", positionList.size()));

        List<Position> positions = new ArrayList<>();
        List<TravelDto> travelDtoList = new ArrayList<>();

        positions.add(positionList.get(0));
        positions.add(positionList.get(1));

        for (int i = 2; i < positionList.size() - 1; i++) {

            Position p1 = positionList.get(i);
            Position p2 = positionList.get(i + 1);

            final Long time = differenceTimeMilliseconds(p1, p2);

            if ((time > 600000) && (positions.size() > 1)) {
                travelDtoList.add(TravelDto.from(positions));
                positions = new ArrayList<>();
                positions.add(p1);

            } else {
                positions.add(p1);
            }
        }

        // Set last position.
        positions.add(positionList.get(positionList.size() - 1));
        travelDtoList.add(TravelDto.from(positions));

        return travelDtoList;
    }

    public HistoricalDto historical(Device device, String date) {
        return filterBySubscription(device, date);
    }

    private HistoricalDto filterBySubscription(Device device, String date) {
        final Subscription subscription = subscriptionService.getByDeviceId(device.getId());
        final List<Position> positions = getByDeviceIdAndDate(device.getId(), date);
        final Date historyDate = DateUtil.getDateFromString(date);
        final StatusEnum subscriptionStatus = subscription.getStatus();

        if (subscriptionStatus == StatusEnum.ENABLED && !positions.isEmpty())
            return HistoricalDto.from(positions, subscription.getDevice(), subscription);

        if (subscriptionStatus == StatusEnum.ENABLED && positions.isEmpty())
            return HistoricalDto.withSubscription(subscription);

        if (subscriptionStatus == StatusEnum.DISABLED)
            return HistoricalDto.withoutSubscription(device);

        if ((subscriptionStatus == StatusEnum.EXPIRED) && historyDate.after(subscription.getEndDate()))
            return HistoricalDto.withSubscription(subscription);

        if ((subscriptionStatus == StatusEnum.EXPIRED) && historyDate.before(subscription.getEndDate()) && positions.isEmpty())
            return HistoricalDto.withSubscription(subscription);

        return HistoricalDto.from(positions, subscription.getDevice(), subscription);
    }

    public ReportDetailedDto reportDetailed(List<Position> positionList) {
        List<TravelDto> travelDtoList = travelDtoList(positionList);
        return ReportDetailedDto.fromPositionList(travelDtoList);
    }

    public void delete(Long deviceId) throws EJBException {
        try {
            positionRepository.deletePositionsByDeviceId(deviceId);
        } catch (PersistenceException e) {
            log.error("[ERROR] PersistenceException: " + e.getMessage());
        }
    }

    private long differenceTimeMilliseconds(Position position1, Position position2) {
        return (position2.getTime().getTime() - position1.getTime().getTime());
    }
}
