package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FenceDto implements Serializable {

    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private Boolean entireDay;
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean enteringZone;
    private Boolean leavingZone;
    private Boolean status;
    private String emails;
    private String cellphones;
    private Long deviceId;
    private List<CoordinateDto> coordinateList;
    private boolean hasCredit;

    public static FenceDto createNew() {
        FenceDto fenceDto = new FenceDto();
        fenceDto.id = EntityUtil.DEFAULT_LONG;
        fenceDto.name = EntityUtil.DEFAULT_STRING;
        fenceDto.startTime = EntityUtil.DEFAULT_STRING;
        fenceDto.endTime = EntityUtil.DEFAULT_STRING;
        fenceDto.entireDay = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.sunday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.monday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.tuesday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.wednesday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.thursday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.friday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.saturday = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.enteringZone = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.leavingZone = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.status = EntityUtil.DEFAULT_BOOLEAN;
        fenceDto.emails = EntityUtil.DEFAULT_STRING;
        fenceDto.cellphones = EntityUtil.DEFAULT_STRING;

        return fenceDto;
    }

    public static FenceDto fromFenceEntity(GeoFence geoFence, boolean hasCredit) {
        FenceDto fenceDto = createNew();
        fenceDto.id = geoFence.getId();
        fenceDto.name = geoFence.getName();
        fenceDto.startTime = DateUtil.getStringTimeFromDate(geoFence.getStartTime());
        fenceDto.endTime = DateUtil.getStringTimeFromDate(geoFence.getEndTime());
        fenceDto.entireDay = geoFence.getEntireDay();
        fenceDto.sunday = geoFence.getSunday();
        fenceDto.monday = geoFence.getMonday();
        fenceDto.tuesday = geoFence.getTuesday();
        fenceDto.wednesday = geoFence.getWednesday();
        fenceDto.thursday = geoFence.getThursday();
        fenceDto.friday = geoFence.getFriday();
        fenceDto.saturday = geoFence.getSaturday();
        fenceDto.enteringZone = geoFence.getEnteringZone();
        fenceDto.leavingZone = geoFence.getLeavingZone();
        fenceDto.status = geoFence.getStatus().equals(StatusEnum.ENABLED);
        fenceDto.emails = geoFence.getEmails();
        fenceDto.cellphones = geoFence.getCellphones();
        fenceDto.deviceId = geoFence.getDevice().getId();
        fenceDto.hasCredit = hasCredit;
        return fenceDto;
    }

    public static GeoFence fenceEntityFromFenceDto(FenceDto fenceDto) {
        GeoFence geoFenceEntity = GeoFence.createNew();
        Date startTime;
        Date endTime;
        if (StringUtil.isNotEmpty(fenceDto.getStartTime())) {
            startTime = DateUtil.setZeroMinute(DateUtil.getTimeFromString(fenceDto.getStartTime()));
        } else {
            startTime = DateUtil.setZeroHour(new Date());
        }

        if (StringUtil.isNotEmpty(fenceDto.getEndTime())) {
            endTime = DateUtil.set59Minnute(DateUtil.getTimeFromString(fenceDto.getEndTime()));
        } else {
            endTime = DateUtil.set24Hour(new Date());
        }
        geoFenceEntity.setId(fenceDto.getId());
        geoFenceEntity.setName(fenceDto.getName());
        geoFenceEntity.setStartTime(startTime);
        geoFenceEntity.setEndTime(endTime);
        geoFenceEntity.setEntireDay(fenceDto.getEntireDay());
        geoFenceEntity.setSunday(fenceDto.getSunday());
        geoFenceEntity.setMonday(fenceDto.getMonday());
        geoFenceEntity.setTuesday(fenceDto.getTuesday());
        geoFenceEntity.setWednesday(fenceDto.getWednesday());
        geoFenceEntity.setThursday(fenceDto.getThursday());
        geoFenceEntity.setFriday(fenceDto.getFriday());
        geoFenceEntity.setSaturday(fenceDto.getSaturday());
        geoFenceEntity.setLeavingZone(fenceDto.getLeavingZone());
        geoFenceEntity.setEnteringZone(fenceDto.getEnteringZone());
        geoFenceEntity.setStatus(fenceDto.getStatus() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        geoFenceEntity.setEmails(fenceDto.getEmails());
        geoFenceEntity.setCellphones(fenceDto.getCellphones());
        geoFenceEntity.setDevice(new Device(fenceDto.getDeviceId()));

        return geoFenceEntity;
    }

    public List<Coordinate> coordinatesListFromFence() {
        List<Coordinate> coordinateList = new ArrayList<>();
        if (getCoordinateList() == null) {
            return new ArrayList<>();
        }
        for (CoordinateDto coordinateDto : getCoordinateList()) {
            Coordinate coordinate = new Coordinate();
            coordinate.setGeoFence(new GeoFence(getId()));
            coordinate.setLatitude(Double.parseDouble(coordinateDto.getLatitude()));
            coordinate.setLongitude(Double.parseDouble(coordinateDto.getLongitude()));
            coordinateList.add(coordinate);
        }

        return coordinateList;
    }

    public GeoFence convertToFenceEntity(Optional<GeoFence> fenceOptional) {
        GeoFence geoFenceEntity = fenceOptional.isPresent() ? fenceOptional.get() : GeoFence.createNew();
        Date startTime;
        Date endTime;
        if (StringUtil.isNotEmpty(getStartTime())) {
            startTime = DateUtil.setZeroMinute(DateUtil.getTimeFromString(getStartTime()));
        } else {
            startTime = DateUtil.setZeroHour(new Date());
        }

        if (StringUtil.isNotEmpty(getEndTime())) {
            endTime = DateUtil.set59Minnute(DateUtil.getTimeFromString(getEndTime()));
        } else {
            endTime = DateUtil.set24Hour(new Date());
        }
        geoFenceEntity.setId(getId());
        geoFenceEntity.setName(getName());
        geoFenceEntity.setStartTime(startTime);
        geoFenceEntity.setEndTime(endTime);
        geoFenceEntity.setEntireDay(getEntireDay());
        geoFenceEntity.setSunday(getSunday());
        geoFenceEntity.setMonday(getMonday());
        geoFenceEntity.setTuesday(getTuesday());
        geoFenceEntity.setWednesday(getWednesday());
        geoFenceEntity.setThursday(getThursday());
        geoFenceEntity.setFriday(getFriday());
        geoFenceEntity.setSaturday(getSaturday());
        geoFenceEntity.setLeavingZone(getLeavingZone());
        geoFenceEntity.setEnteringZone(getEnteringZone());
        geoFenceEntity.setStatus(getStatus() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        geoFenceEntity.setEmails(getEmails());
        geoFenceEntity.setCellphones(getCellphones());
        geoFenceEntity.setDevice(new Device(getDeviceId()));
        return geoFenceEntity;

    }

    public static FenceDto fromFenceEntity(GeoFence geoFence, List<Coordinate> coordinateList, boolean hasCredit) {
        FenceDto fenceDto = fromFenceEntity(geoFence, hasCredit);
        fenceDto.coordinateList = new ArrayList<>();
        fenceDto.coordinateList.addAll(coordinateList.stream()
                .map(CoordinateDto::fromCoordinate)
                .collect(Collectors.toList()));
        return fenceDto;
    }
}
