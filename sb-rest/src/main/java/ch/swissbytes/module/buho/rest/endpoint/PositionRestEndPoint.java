package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dto.HistoricalDto;
import ch.swissbytes.domain.dto.PositionDto;
import ch.swissbytes.domain.dto.ReportDetailedDto;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.position.model.Position;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import ch.swissbytes.module.shared.utils.DateUtil;
import ch.swissbytes.module.shared.utils.PositionUtil;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("position")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PositionRestEndPoint {

    @Inject
    private Logger log;
    @Inject
    private Identity identity;
    @Inject
    private PositionService positionService;
    @Inject
    private DeviceService deviceService;
    @Inject
    private PositionRepository positionRepository;
    @Inject
    private DeviceRepository deviceRepository;

    @GET
    @Path("/string-date-server")
    @LoggedIn
    public Response getStringDateServer() {
        try {
            log.info("========= Get server date ");
            final String serverDateFormatted = DateUtil.getStringDateFromDate(new Date());
            log.info("response: " + serverDateFormatted);
            return Response.ok().entity(serverDateFormatted).build();

        } catch (RuntimeException e) {
            log.error("Error while give server date ");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/list/last")
    @RolesAllowed({"current-position"})
    public Response findLastByUserId() {
        try {
            log.info("************* LAST POSITION OF DEVICES ************");
            log.info("UserId: " + identity.getIdentityDto().getId());

            List<PositionDto> response = positionService.lastOfDevicesByUserId(identity.getIdentityDto().getId());

            return Response.ok().entity(response).build();

        } catch (RuntimeException rte) {
            log.error("[ERROR]: " + rte.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{deviceId}/report/{date}/detailed")
    public Response findAllPosition(@PathParam("deviceId") Long deviceId, @PathParam("date") final String strDate) {
        try {

            log.info("***************** REPORT DETAILED *****************");

            Optional<Device> deviceOptional = deviceRepository.getById(deviceId);

            if (deviceOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("device not found").build();
            }

            List<Position> positionList = positionService.getByDeviceIdAndDate(deviceId, strDate);

            if (positionList.isEmpty() || positionList.size() == 1) {
                return Response.ok(ReportDetailedDto.createNew()).build();
            }

            // Filter by time (10 minutes stopped)
            ReportDetailedDto reportDetailed = positionService.reportDetailed(positionList);

            return Response.ok().entity(reportDetailed).build();

        } catch (RuntimeException rte) {
            log.error("[ERROR detailed report]: " + rte.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GET
    @Path("/historic/{deviceId}/{date}")
    @RolesAllowed({"history"})
    public List<PositionDto> findPositionsByDeviceIdAndDate(@PathParam("deviceId") Long deviceId,
                                                            @PathParam("date") final String strDate) {
        log.info("findPositionsByDeviceIdAndDate " + deviceId + " " + strDate);

        List<Position> positionList = positionService.getByDeviceIdAndDate(deviceId, strDate);

        if (positionList.isEmpty() || positionList.size() == 1) {
            return Collections.emptyList();
        }

        return fromPositionEntity(positionList);
    }

    @GET
    @Path("/list-report-by-device/{deviceId}/{startDate}/{endDate}")
    @RolesAllowed({"reports"})
    public Response getPositionsBetweenDatesByDevice(@PathParam("deviceId") Long deviceId,
                                                     @PathParam("startDate") String startDate,
                                                     @PathParam("endDate") String endDate) {
        log.info("getPositionsBetweenDatesByDevice..." + deviceId + " - " + startDate + " - " + endDate);
        try {
            Date fromDate = DateUtil.setZeroHour(DateUtil.getDateFromString(startDate));
            Date toDate = DateUtil.set24Hour(DateUtil.getDateFromString(endDate));

            List<PositionDto> positionDtos = new ArrayList<>();
            if (positionRepository.hasPositionInDates(deviceId, fromDate, toDate)) {
                positionDtos = fromPositionEntity(positionRepository.findByDeviceIdAndDate(deviceId, fromDate, toDate));
            }

            return Response.ok().entity(positionDtos).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).entity("Las fechas ingresadas son incorrectas").build();
        }
    }

    @GET
    @Path("{deviceId}/{date}")
    public Response historicalPositions(@PathParam("deviceId") Long deviceId, @PathParam("date") String date) {
        try {
            log.info("*************** HISTORICAL POSITION ****************");
            log.info(String.format("deviceId: %s date: %s", deviceId, date));

            Optional<Device> deviceOptional = deviceRepository.getById(deviceId);

            if (!DateUtil.isValidDate(date)) {
                return Response.status(Status.BAD_REQUEST).entity("formato de la fecha invalido.").build();
            }

            if (deviceOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("Dispositivo no encontrado").build();
            }

            Device device = deviceOptional.get();

            HistoricalDto historicalDto = positionService.historical(device, date);

            return Response.ok().entity(historicalDto).build();

        } catch (InternalServerErrorException eex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();

        }
    }

    @DELETE
    @Path("/backend/delete/{deviceId}")
    @RolesAllowed({"device-management"})
    public Response deletePositions(@PathParam("deviceId") Long deviceId) {
        try {

            log.info("*************** DELETE POSITIONS BY DEVICE ***************");
            log.info("deviceId: " + deviceId);

            positionService.delete(deviceId);
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Response.Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    private List<PositionDto> fromPositionEntity(List<Position> positionList) {
        Double maxSpeed = PositionUtil.maxSpeed(positionList);
        Double avgSpeed = PositionUtil.avgSpeed(positionList);
        Double distance = PositionUtil.distance(positionList);

        List<PositionDto> positionDtoList = new ArrayList<>();
        int index = 1;
        for (Position position : positionList) {
            position.setMaxSpeed(maxSpeed);
            position.setAvgSpeed(avgSpeed);
            position.setDistance(distance);
            positionDtoList.add(PositionDto.fromPosition(position, position.getDevice(), index));
            index++;
        }
        return positionDtoList;
    }
}
