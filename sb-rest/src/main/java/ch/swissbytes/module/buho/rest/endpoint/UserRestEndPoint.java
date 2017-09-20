package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dto.ChangePasswordDto;
import ch.swissbytes.domain.dto.UserManagementDto;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.model.UserDevice;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userdevice.service.UserDeviceService;
import ch.swissbytes.module.buho.app.userrole.model.UserRole;
import ch.swissbytes.module.buho.app.userrole.respository.UserRoleRepository;
import ch.swissbytes.module.buho.app.userrole.service.UserRoleService;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.ResourceMessage;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
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
import java.util.List;

@Path("users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class UserRestEndPoint {
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("usuario");

    @Inject
    private Logger log;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private UserService userService;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private UserDeviceService userDeviceService;
    @Inject
    private Identity identity;
    @Inject
    private UserRoleService userRoleService;

    @GET
    @LoggedIn
    @RolesAllowed({"user-management"})
    public Response findAllUsers() {
        log.info("==============> LIST BY USER LOGGED");
        log.info("userId: " + identity.getIdentityDto().getId());

        List<User> users = userService.findByUserId(identity.getIdentityDto().getId());
        List<UserManagementDto> userList = fromUserEntityList(users);
        if (userList.isEmpty()) {
            return Response.ok().build();
        } else {
            return Response.ok().entity(userList).build();
        }
    }

    @POST
    @RolesAllowed({"user-management"})
    public Response createUser(UserManagementDto dto) {
        try {
            log.info("===============> CREATE USER");
            log.info("input: " + dto);


            User user = dto.toUserEntity();
            user.setAccount(new Account(identity.getIdentityDto().getAccountId()));
            List<UserDevice> userDeviceList = dto.toUserDeviceList();
            Long userId = userService.storeUser(user, userDeviceList).getId();
            userRoleService.storeUserRole(new UserRole(userId, dto.getRoleId()));

            return Response.ok().entity(userId).build();

        } catch (InvalidInputException ejbEx) {
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_ACCEPTABLE).entity(ejbEx.getMessage()).build();
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
    @RolesAllowed({"user-management"})
    public Response updateUsers(UserManagementDto[] userManagementDtoList) {
        try {
            log.info("===============> UPDATE USER ");
            log.info("input: " + userManagementDtoList.length);

            userService.updateFrom(userManagementDtoList);
            return Response.ok().build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/{id}")
    @RolesAllowed({"user-management"})
    public Response deleteUser(@PathParam("id") Long userId) {
        try {
            log.info("===============> DELETE USER ");
            log.info("userId: " + userId);
            Optional<User> userEntityOptional = userRepository.getById(userId);

            if (userEntityOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                userService.delete(userEntityOptional.get());
            }
            return Response.ok().build();

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
    @Path("/reset/password")
    @RolesAllowed({"user-management", "account-management"})
    public Response changePasswordAdmin(ChangePasswordDto dto) {
        try {
            log.info("===============> RESET USER PASSWORD ");
            log.info("input: " + dto);

            Optional<User> userOptional = userRepository.getById(dto.getUserId());
            if (userOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("user not exist!").build();
            }

            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                return Response.status(Status.BAD_REQUEST).entity("user password are not equals!").build();
            }

            userService.resetPasswordBySysAdmin(dto.getNewPassword(), userOptional.get());
            return Response.ok().build();

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
    @Path("/password")
    @LoggedIn
    public Response changePassword(UserManagementDto userSecurityDto) {
        try {
            log.info("===============> CHANGE USER PASSWORD ");
            log.info("input: " + userSecurityDto);

            Optional<User> userOptional = userRepository.getByIdAndPassword(identity.getIdentityDto().getId(), userSecurityDto.getPassword());
            if (userOptional.isAbsent()) {
                throw new InvalidInputException("no exist user by id: " + userSecurityDto.getId());
            }
            User user = userOptional.get();
            user.setPassword(userSecurityDto.getPasswordNew());
            userService.changePassword(userOptional.get());
            return Response.ok().build();
        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.NOT_FOUND).entity(invalidInputException.getMessage()).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private List<UserManagementDto> fromUserEntityList(List<User> users) {
        List<UserManagementDto> userManagementDtoList = new ArrayList<>();

        for (User user : users) {
            List<UserDevice> devices = userDeviceRepository.findByUserId(user.getId());
            UserManagementDto userManagementDto = UserManagementDto.fromUserEntity(user, devices);

            List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
            userManagementDto.setRoleId(getRolesIdList(userRoles));

            userManagementDto.setAccountAdmin(isCompanyAdmin(userRoles));
            userManagementDto.setSystemAdmin(isSystemAdmin(userRoles));

            userManagementDtoList.add(userManagementDto);
        }

        return userManagementDtoList;
    }

    private boolean isCompanyAdmin(List<UserRole> userRoles) {
        for (UserRole entity : userRoles)
            if (entity.getRole().getId() == 2)
                return true;
        return false;
    }

    private boolean isSystemAdmin(List<UserRole> userRoles) {
        for (UserRole entity : userRoles)
            if (entity.getRole().getId() == 1)
                return true;
        return false;
    }

    private Long getRolesIdList(List<UserRole> userRoles) {

        if (userRoles.isEmpty()) return 3L;
        for (UserRole entity : userRoles)
            if (entity.getRole().getVisible())
                return entity.getRole().getId();
        return userRoles.get(0).getRole().getId();
    }
}
