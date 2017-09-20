package ch.swissbytes.module.buho.rest.endpoint;


import ch.swissbytes.domain.dto.UserDto;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("app")
public class ApplicationRestEndpoint {

    @Inject
    private Logger log;


    @GET
    @Path("status")
    @Produces({MediaType.APPLICATION_JSON})
    public Response checkStatusApp() {
        try {
            log.info("********* Check status APP ********");
            return Response.ok("UP! :)").build();

        } catch (ResponseProcessingException e) {
            log.error("[ERROR] ResponseProcessingException: " + e.getMessage());
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("test/put/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestPUTest(@PathParam("id") Long id, UserDto userDto) {
        log.info("************* TEST APP ***************");
        log.info("id: " + id);
        log.info("userDto: " + userDto);
        return Response.ok().entity(userDto).build();
    }
}
