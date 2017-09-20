package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dto.CustomerDto;
import ch.swissbytes.domain.dto.LinkingInvitationDto;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.notifications.mail.template.device.smartphone.EmailSenderLinkingSmartphone;
import ch.swissbytes.module.shared.notifications.mail.template.EmailSenderPayInOffice;
import ch.swissbytes.module.shared.notifications.mail.template.subscription.MailSenderSubscriptionExpired;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.utils.LongUtil;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("notification")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class NotificationRestEndPoint {

    @Inject
    private Logger log;

    @Inject
    private EmailSenderLinkingSmartphone emailSenderLinkingSmartphone;

    @Inject
    private MailSenderSubscriptionExpired mailSenderSubscriptionExpired;

    @Inject
    private EmailSenderPayInOffice emailSenderPayInOffice;

    @Inject
    private UserService userService;

    @Inject
    private DeviceRepository deviceRepository;


    @POST
    @Path("/linking")
    public Response invitationToLinking(LinkingInvitationDto linkingInvitationDto) {
        try {

            log.info("*************** INVITATION TO LINK SMARTPHONE ***************");
            log.info("input: " + linkingInvitationDto);

            if (StringUtil.isEmpty(linkingInvitationDto.getEmail())
                    || StringUtil.isEmpty(linkingInvitationDto.getImei())) {
                return Response.noContent().entity("email its empty").build();
            }

            Optional<Device> deviceOptional = deviceRepository.getByImei(linkingInvitationDto.getImei());

            if (deviceOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            emailSenderLinkingSmartphone.createEmailNotification(linkingInvitationDto.getEmail(), deviceOptional.get());
            return Response.ok().build();

        } catch (InvalidInputException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("subscription")
    public Response subscriptionExpired(String imei) {

        log.info("*************** EXPIRATION SMARTPHONE ***************");
        log.info("imei: " + imei);

        Optional<Device> deviceOptional = deviceRepository.getByImei(imei);

        if (deviceOptional.isAbsent()) {
            return Response.status(Status.NOT_FOUND).entity("device not found").build();
        }

        Device device = deviceOptional.get();

        if (device.getAccount() == null) {
            return Response.status(Status.BAD_REQUEST).entity("device hasn't account").build();
        }

        mailSenderSubscriptionExpired.createEmailNotification(device);
        return Response.ok().build();
    }

    @POST
    @Path("/customer")
    public Response invitationMail(CustomerDto customerDto) {
        log.info("************* REQUEST CUSTOMER **************");
        log.info("customer: " + customerDto);

        if (customerDto.getName().isEmpty() || customerDto.getPhoneNumber().isEmpty()) {
            Response.status(Status.BAD_REQUEST).build();
        }

        if (LongUtil.isNumeric(customerDto.getPhoneNumber())) {
            Response.status(Status.NOT_ACCEPTABLE).entity("phone number not valid").build();
        }

        emailSenderPayInOffice.createEmailNotification(customerDto);

        return Response.ok(true).build();
    }

    @POST
    @Path("test")
    public Response testPost(String parameter1, String parameter2) {
        log.info("parameter: " + parameter1);
        log.info("parameter: " + parameter2);
        return Response.ok(parameter1, parameter2).build();
    }
}