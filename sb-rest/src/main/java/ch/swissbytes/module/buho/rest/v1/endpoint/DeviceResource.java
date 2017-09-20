package ch.swissbytes.module.buho.rest.v1.endpoint;

import ch.swissbytes.module.buho.app.device.exception.DeviceNotFoundException;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.rest.v1.dto.CurrentPositionDto;
import ch.swissbytes.module.shared.exception.UserNotFoundException;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("devices")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Stateless
public class DeviceResource {

    @Inject
    private Logger log;
    @Inject
    private DeviceService deviceService;
    @Inject
    private Identity identity;


    @GET
    @Path("/positions/current")
    @LoggedIn
    public Response getDevices() {

        try {

            final Long userId = identity.getIdentityDto().getId();
            final List<CurrentPositionDto> response = deviceService.currentPositionOfDevicesByUserId(userId);
            return Response.ok().entity(response).build();


        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (RuntimeException e) {
            log.error("[ERROR] RuntimeException: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("{id}/positions/current")
    public Response getPositionsByDeviceId(@PathParam("id") Long id) {
        log.info("Get current position by device id: " + id);

        try {
            Device device = deviceService.findById(id);
            CurrentPositionDto response = deviceService.getCurrentPositionByDeviceId(device);
            return Response.ok().entity(response).build();

        } catch (DeviceNotFoundException e) {
            log.error("Dispositivo no encontrado: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity("dispositivo no encontrado").build();

        } catch (RuntimeException e) {
            log.error("[ERROR] RuntimeException: " + e.getMessage());
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}