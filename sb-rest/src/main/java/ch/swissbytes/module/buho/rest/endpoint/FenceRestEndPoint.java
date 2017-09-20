package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.CoordinateDao;
import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.dto.FenceDto;
import ch.swissbytes.domain.entities.Coordinate;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.buho.app.geofence.model.GeoFence;
import ch.swissbytes.module.buho.app.geofence.repository.GeoFenceRepository;
import ch.swissbytes.module.buho.app.geofence.service.FenceService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

@Path("fences")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class FenceRestEndPoint {

    @Inject
    private Logger log;

    @Inject
    private FenceService fenceService;

    @Inject
    private GeoFenceRepository fenceRepository;

    @Inject
    private CoordinateDao coordinateDao;

    @Inject
    private SmsControlDao smsControlDao;

    @GET
    @Path("devices/{id}")
    @RolesAllowed({"control"})
    public List<FenceDto> findFenceByDeviceId(@PathParam("id") Long id) {
        log.info("********* Find fence by device Id ************");

        Optional<SmsControl> smsControlOptional = smsControlDao.getCurrentByDeviceId(id);
        boolean hasCredit = true;
        if (smsControlOptional.isPresent()) {
            hasCredit = smsControlOptional.get().getUsedSms() < smsControlOptional.get().getMaxSms();
        }
        return fromFenceEntityList(fenceRepository.findByDeviceId(id), hasCredit);
    }

    @POST
    @RolesAllowed({"control"})
    public Response createFence(FenceDto fenceDto) {
        try {
            log.info("********* Create fence *********");
            log.info("FenceDto: " + fenceDto);
            GeoFence geoFence = FenceDto.fenceEntityFromFenceDto(fenceDto);
            List<Coordinate> coordinateList = fenceDto.coordinatesListFromFence();
            Long fenceId = fenceService.storeFence(geoFence, coordinateList).getId();
            return Response.ok(fenceId).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @RolesAllowed({"control"})
    public Response updateFences(FenceDto[] fenceDtoList) {
        try {
            log.info("********* update Fence ********");
            log.info("input: " + fenceDtoList.length);

            List<GeoFence> geoFenceList = new ArrayList<>();

            for (FenceDto fenceDto : fenceDtoList) {
                Optional<GeoFence> fenceOptional = fenceRepository.getById(fenceDto.getId());
                GeoFence geoFence = fenceDto.convertToFenceEntity(fenceOptional);
                geoFence.setCoordinateList(fenceDto.coordinatesListFromFence());
                geoFenceList.add(geoFence);
            }

            String responseError = fenceService.storeFence(geoFenceList);

            if (!responseError.isEmpty()) {
                throw new InvalidInputException(responseError.toString());
            }

            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"control"})
    public Response deleteFence(@PathParam("id") Long id) {
        try {
            log.info("********* Delete Fence *********");
            log.info("id: " + id);

            Optional<GeoFence> fenceOptional = fenceRepository.getById(id);

            if (fenceOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            fenceService.remove(fenceOptional.get());

            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
        }
    }

    private List<FenceDto> fromFenceEntityList(List<GeoFence> geoFenceList, boolean hasCredit) {
        List<FenceDto> fenceDtoList = new ArrayList<>();
        for (GeoFence geoFence : geoFenceList) {
            List<Coordinate> coordinateList = coordinateDao.findByFenceId(geoFence.getId());
            fenceDtoList.add(FenceDto.fromFenceEntity(geoFence, coordinateList, hasCredit));
        }
        return fenceDtoList;
    }
}
