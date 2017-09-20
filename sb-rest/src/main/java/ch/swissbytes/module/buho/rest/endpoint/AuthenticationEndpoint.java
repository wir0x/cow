package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.ViewerDao;
import ch.swissbytes.domain.dto.ChangePasswordDto;
import ch.swissbytes.domain.dto.UserManagementDto;
import ch.swissbytes.domain.entities.VerificationTokenEntity;
import ch.swissbytes.domain.entities.Viewer;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.configuration.repository.ConfigurationRepository;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.device.service.DeviceService;
import ch.swissbytes.module.buho.app.position.repository.PositionRepository;
import ch.swissbytes.module.buho.app.position.service.PositionService;
import ch.swissbytes.module.buho.app.role.model.Role;
import ch.swissbytes.module.buho.app.role.repository.RoleRepository;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.buho.service.VerificationTokenService;
import ch.swissbytes.module.buho.service.ViewerService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.notifications.mail.template.passwordrecovery.EmailSenderPasswordRecovery;
import ch.swissbytes.module.shared.notifications.mail.template.account.EmailSenderRegisterAccount;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import ch.swissbytes.module.shared.rest.security.dto.JWTClaimSetDto;
import ch.swissbytes.module.shared.rest.security.dto.UsernamePasswordCredential;
import ch.swissbytes.module.shared.rest.security.token.TokenManager;
import net.minidev.json.JSONObject;
import org.jboss.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/authenticate")
@Stateless
public class AuthenticationEndpoint {

    public static final String USERNAME_PASSWORD_WEB_CREDENTIAL_CONTENT_TYPE = "application/x-authc-username-password-web+json";
    public static final String USERNAME_PASSWORD_MOBILE_CREDENTIAL_CONTENT_TYPE = "application/x-authc-username-password-mobile+json";

    @Inject
    private Logger log;
    @Inject
    private AccountRepository accountDao;
    @Inject
    private Identity identity;
    @Inject
    private TokenManager tokenManager;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private AccountService accountService;
    @Inject
    private VerificationTokenService verificationTokenService;
    @Inject
    private EmailSenderPasswordRecovery emailSender;
    @Inject
    private ConfigurationRepository configurationRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private ViewerDao viewerDao;
    @Inject
    private ViewerService viewerService;
    @Inject
    private PositionService positionService;
    @Inject
    private PositionRepository positionRepository;
    @Inject
    private UserRoleService userRoleService;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private EmailSenderRegisterAccount emailSenderRegisterAccount;
    @Inject
    private DeviceService deviceService;


    @POST
    @Consumes({USERNAME_PASSWORD_WEB_CREDENTIAL_CONTENT_TYPE})
    public Response authenticate(UsernamePasswordCredential credential) {
        log.info("************* AUTHENTICATE FROM WEB  ************");
        log.info("input: " + credential);
        if (identity.isLoggedIn()) {
            return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
        }

        Optional<User> userOptional = userRepository.login(credential.getUserId(), credential.getPassword());
        if (userOptional.isAbsent())
            userOptional = userRepository.loginEmail(credential.getUserId(), credential.getPassword());

        if (userOptional.isAbsent())
            return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN).build();

        User user = userOptional.get();

        if (credential.getUniqueId() != null) {
            if (userService.isLinkedUser(user.getId(), credential.getUniqueId()))
                return responseLinkedAnotherDevice(user);

            if (userService.isLinkedDevice(user.getId(), credential.getUniqueId()))
                return responseLinkedAnotherUser(user);
            deviceService.linkingDevice(user.getId(), credential.getUniqueId());
        }

        IdentityDto identityDto = getIdentityDto(user);
        return authenticate(identityDto, credential, user);
    }


    @POST
    @Consumes({USERNAME_PASSWORD_MOBILE_CREDENTIAL_CONTENT_TYPE})
    public Response authenticateFromViewerApp(UsernamePasswordCredential credential) {
        log.info("************* AUTHENTICATE FROM VIEWER ************");
        log.info("input: " + credential);

        if (identity.isLoggedIn())
            return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();

        Optional<User> userOptional = userRepository.login(credential.getUserId(), credential.getPassword());

        if (userOptional.isAbsent())
            userOptional = userRepository.loginEmail(credential.getUserId(), credential.getPassword());

        if (userOptional.isAbsent())
            return Response.status(Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN).build();

        User user = userOptional.get();
        IdentityDto identityDto = getIdentityDto(user);
        return authenticate(identityDto, credential, user);
    }

    @GET
    @Path("/app/login/{uniqueId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response loginWithUniqueId(@PathParam("uniqueId") String uniqueId) {
        try {
            log.info("************* LOGIN WITH UNIQUE ID ***************");
            log.info("uniqueId: " + uniqueId);
            Optional<Device> deviceOpt = deviceRepository.getByImei(uniqueId);

            if (deviceOpt.isAbsent()) return Response.status(Response.Status.NOT_FOUND).build();

            List<UserDevice> userDeviceList = userDeviceRepository.findByDeviceId(deviceOpt.get().getId());

            if (userDeviceList.isEmpty()) return Response.ok().build();

            IdentityDto identityDto = getIdentityDto(userDeviceList.get(0).getUser());
            return authenticate(identityDto);

        } catch (Exception e) {

            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/create")
    public Response registerAccount(UserManagementDto userManagementDto) {
        try {
            log.info("*********** CREATE ACCOUNT **********");
            log.info("input: " + userManagementDto);
            if (userService.isLinkedDevice(userManagementDto.getId(), userManagementDto.getImei()))
                return deviceLinkedAnotherAccount();

            User user = accountService.storeAccount(userManagementDto);
            IdentityDto identityDto = getIdentityDto(user);

            /* Send mail to user registered */
            emailSenderRegisterAccount.createEmailNotification(user);

            return authenticate(identityDto);

        } catch (InvalidInputException ejbEx) {
            log.error("[ERROR] InvalidInputException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_ACCEPTABLE).entity(ejbEx.getMessage()).build();
        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/create/web/account")
    public Response createAccountFromWeb(UserManagementDto userManagementDto) {
        try {
            log.info("*********** CREATE ACCOUNT FRONTEND **********");
            log.info("input: " + userManagementDto);

            validateParameterAccount(userManagementDto);

            User user = accountService.storeAccountFromWeb(userManagementDto);
            IdentityDto identityDto = getIdentityDto(user);

            // Register smartphone if in the request is send the imei.
            if (userManagementDto.getImei() != null) {
                String imei = userManagementDto.getImei();
                deviceService.linkingSmartphone(identityDto.getId(), imei);
            }

            // Send success registration email to user.
            emailSenderRegisterAccount.createEmailNotification(user);

            return authenticate(identityDto);

        } catch (InvalidInputException ejbEx) {

            log.warn("[ERROR] InvalidInputException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_ACCEPTABLE).entity(ejbEx.getMessage()).build();

        } catch (EJBException ejbEx) {

            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/password-recovery")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sendLostPasswordToken(String email) {
        try {
            log.info("************* Send Lost Password Token ************");
            log.info("email: " + email);

            User user = userService.validateEmail(email);
            VerificationTokenEntity tokenEntity = verificationTokenService.getVerificationTokenGenerated(user);
            StringBuilder builder = new StringBuilder();
            builder.append(configurationRepository.getUrlResetPassword())
                    .append(tokenEntity.getToken());
            String link = builder.toString();
            String multipart = emailSender.createMimeMultipart(link, user);
            emailSender.sendMail(user.getEmail(), multipart);
            log.info("Token was generated and sent to email");
            return Response.ok(email).build();

        } catch (RuntimeException e) {

            log.error("[ERROR] RuntimeException: " + e.getMessage(), e);
            return Response.status(Status.NOT_FOUND).entity(email).build();

        } catch (Exception e) {

            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/password-token/{token}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getPasswordToken(@PathParam("token") String token) {
        try {
            log.info("*********** Get Password Token ***********");
            log.info("token: " + token);
            VerificationTokenEntity tokenEntity = verificationTokenService.getActiveVerificationToken(token);
            if (tokenEntity == null) {
                return Response.status(Status.NOT_FOUND).entity("Token is expired or is already used").build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/reset-password")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response resetPassword(UserManagementDto userManagementDto) {
        try {

            log.info("************ Reset Password ***********");
            log.info("UserManagementDto: " + userManagementDto);

            VerificationTokenEntity tokenEntity = verificationTokenService.getActiveVerificationToken(userManagementDto.getToken());

            if (tokenEntity == null) {
                return Response.status(Status.NOT_FOUND).entity("Token not found or is already used").build();
            }

            Optional<User> userOptional = userRepository.getById(tokenEntity.getUser().getId());

            if (userOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("User not exist!.").build();
            }

            User user = userOptional.get();
            user.setPassword(userManagementDto.getPassword());
            userService.changePasswordWithToken(userOptional.get(), tokenEntity);
            return Response.ok().build();

        } catch (Exception e) {

            log.error("[ERROR]: Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/change-password")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response changesPassword(ChangePasswordDto changePasswordDto) {
        try {
            log.info("*********** CHANGE PASSWORD ***********");
            log.info("Input: " + changePasswordDto);

            userService.changePassword(changePasswordDto).getId();
            return Response.ok(Boolean.TRUE).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("/check-session-time")
    @Produces({MediaType.APPLICATION_JSON})
    public Response checkSessionTime() {

        log.info("********** CHECK SESSION TIME **********");

        JSONObject checkSessionTime = new JSONObject();
        checkSessionTime.put("status", identity.isLoggedIn());
        return Response.ok().entity(checkSessionTime).build();
    }

    @DELETE
    @Path("/logout/{uniqueId}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response disableViewer(@PathParam("uniqueId") String uniqueId) {
        try {
            log.info("********* Disable App viewer by UniqueId **********");
            log.info("uniqueId: " + uniqueId);
            Optional<Viewer> viewerOpt = viewerDao.getByUniqueId(uniqueId);
            if (viewerOpt.isAbsent()) return Response.status(Response.Status.NOT_FOUND).build();
            viewerService.disabled(viewerOpt.get());
            return Response.ok(Boolean.TRUE).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR] EJBException: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e.getMessage(), e);
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    private IdentityDto getIdentityDto(User user) {
        final List<String> permissions = userRepository.findPermissionsByUserId(user.getId());
        final List<Role> roles = userRepository.findRoleByUserId(user.getId());

        IdentityDto identityDto = new IdentityDto();
        identityDto.setId(user.getId());
        identityDto.setEmail(user.getEmail());
        identityDto.setName(user.getName());
        identityDto.setUserName(user.getUsername());
        identityDto.setPermission(permissions);
        identityDto.isBuhoAdmin(userRoleService.userIsBuhoSystemAdmin(user));
        identityDto.isUbidataAdmin(userRoleService.userIsUbidataAdmin(user));
        identityDto.setRoles(roles);
        identityDto.setAccountId(user.getAccount() != null ? user.getAccount().getId() : null);
        identityDto.changePassword(user.getChangePassword());
        identityDto.setIsWithoutExpiration(user.getIsWithoutExpiration());
        identityDto.userLinkedAnotherTracker(false);
        identityDto.setAccountTest(accountService.isTestAccount(identityDto.getAccountId()));
        log.info("identityDto: " + identityDto);
        return identityDto;
    }

    public Response authenticate(IdentityDto identityDto) {
        log.info("authenticate => " + identityDto);
        Response response = Response.status(Status.UNAUTHORIZED).type(MediaType.TEXT_PLAIN).build();
        if (identityDto != null) {
            JWTClaimSetDto jwtClaimSetDto = new JWTClaimSetDto(identityDto);
            String token = tokenManager.generateToken(jwtClaimSetDto);
            identityDto.addAttribute("authctoken", token);
            identity.setIdentityDto(identityDto);
            return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        log.info("response: " + response);
        return response;
    }

    private Response authenticate(IdentityDto identityDto, UsernamePasswordCredential credential, User user) {
        JWTClaimSetDto jwtClaimSetDto = new JWTClaimSetDto(identityDto);
        String token = tokenManager.generateToken(jwtClaimSetDto);
        identityDto.addAttribute("authctoken", token);
        identity.setIdentityDto(identityDto);

        if (credential.getUniqueId() != null && !credential.getUniqueId().isEmpty())
            viewerService.linkUserToAppViewer(user, credential.getUniqueId(), credential.getGcmToken());

        log.info("response: " + identity.getAccountJSON());
        return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private Response deviceLinkedAnotherAccount() {
        IdentityDto identityDto = new IdentityDto();
        identityDto.trackerLinkedAnotherUser(true);
        identity.setIdentityDto(identityDto);
        return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private Response responseLinkedAnotherDevice(User user) {
        log.info("User Linked!!! ");
        IdentityDto identityDto = getIdentityDto(user);
        identityDto.userLinkedAnotherTracker(true);
        identityDto.trackerLinkedAnotherUser(false);
        identity.setIdentityDto(identityDto);
        log.info("response: " + identityDto);
        return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private Response responseLinkedAnotherUser(User user) {
        log.info("Device Linked!!! ");
        IdentityDto identityDto = getIdentityDto(user);
        identityDto.userLinkedAnotherTracker(false);
        identityDto.trackerLinkedAnotherUser(true);
        identity.setIdentityDto(identityDto);
        log.info("response: " + identityDto);
        return Response.ok().entity(identity.getAccountJSON()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private void validateParameterAccount(UserManagementDto userManagementDto) throws InvalidInputException {
        Optional<Account> accountOpt = accountDao.getByName(userManagementDto.getName());
        if (accountOpt.isPresent()) throw new InvalidInputException("La cuenta ya se encuentra registrada");

        Optional<User> userOpt = userRepository.getByEmail(userManagementDto.getEmail());
        if (userOpt.isPresent()) throw new InvalidInputException("El correo electronico ya se encuentra registrado");

        Optional<User> userOpt1 = userRepository.getByUsername(userManagementDto.getUsername());
        if (userOpt1.isPresent()) throw new InvalidInputException("el nombre de usuario ya se encuentra registrado");
    }
}