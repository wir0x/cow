package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.role.repository.RoleRepository;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("group")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class GroupRestEndPoint {

    @Inject
    private Logger log;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private Identity identity;

    @GET
    @Path("/list")
    @LoggedIn
    public Response getRoles() {
        try {
            log.info("===============> ROLE LIST");
            List<Role> roleList = roleRepository.findVisible();
            log.info("response: " + roleList);
            return Response.ok().entity(roleList).build();
        } catch (RuntimeException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
