package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.buho.service.SmsControlService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("sms-control")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class SmsControlRestEndPoint {

    @Inject
    private Logger log;
    @Inject
    private SmsControlDao smsControlDao;
    @Inject
    private SmsControlService smsControlService;

    @GET
    @Path("list/")
    @RolesAllowed({"device-management"})
    public Response getAllSmsControlList() {
        try {
            List<SmsControl> smsControlList = smsControlDao.findAll();
            return Response.ok().entity(smsControlList).build();

        } catch (RuntimeException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/list/{deviceId}")
    @RolesAllowed({"device-management"})
    public Response getSmsControlList(@PathParam("deviceId") Long deviceId) {
        try {
            List<SmsControl> smsControlList = smsControlDao.findByDeviceId(deviceId);
            return Response.ok().entity(smsControlList).build();
        } catch (RuntimeException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/create")
    @RolesAllowed({"device-management"})
    public Response createDevice(SmsControl smsControl) {
        log.info("createSmsControl..." + smsControl.toString());
        try {

            Long smsControlId = smsControlService.store(smsControl).getId();
            return Response.ok(smsControlId).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @RolesAllowed({"device-management"})
    public Response updateDevices(SmsControl smsControl) {
        log.info("updateSmsControl..." + smsControl.toString());
        try {
            Long smsControlId = smsControlService.store(smsControl).getId();
            return Response.ok(smsControlId).build();
        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(invalidInputException.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{smsControlId}")
    @RolesAllowed({"device-management"})
    public Response deleteDevice(@PathParam("smsControlId") Long smsControlId) {
        try {
            smsControlService.delete(smsControlId);
            return Response.ok().build();
        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}