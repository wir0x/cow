package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dto.ContactDto;
import ch.swissbytes.module.buho.service.ContactService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("contact")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class ContactRestEndPoint {

    @Inject
    private Logger log;

    @Inject
    private ContactService contactService;

    @POST
    @Path("/send-mail")
    @LoggedIn
    public Response sendMail(ContactDto contactDto) {
        try {
            log.info("********* Send Mail ********");
            log.info("ContactDto: " + contactDto);
            contactService.createEmail(contactDto);
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
