package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dto.AccountDto;
import ch.swissbytes.domain.enumerator.PaymentStatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.app.account.service.AccountService;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.buho.app.user.repository.UserRepository;
import ch.swissbytes.module.buho.app.user.service.UserService;
import ch.swissbytes.module.buho.app.userdevice.repository.UserDeviceRepository;
import ch.swissbytes.module.buho.app.userrole.respository.UserRoleRepository;
import ch.swissbytes.module.shared.exception.InvalidInputException;
import ch.swissbytes.module.shared.persistence.Optional;
import ch.swissbytes.module.shared.rest.security.Identity;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import net.minidev.json.JSONObject;
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

@Path("accounts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AccountRestEndPoint {

    @Inject
    private Logger log;
    @Inject
    private AccountService accountService;
    @Inject
    private AccountRepository accountDao;
    @Inject
    private UserDeviceRepository userDeviceRepository;
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private Identity identity;

    @GET
    @LoggedIn
    public Response accountsList() {
        try {
            log.info("===============> ACCOUNT LIST");

            List<AccountDto> dto = accountService.findAccountsByUserLogged(identity);
            log.info("accounts: " + dto.size());
            return Response.ok().entity(dto).build();

        } catch (RuntimeException rtEx) {
            log.error("Error!!! RuntimeException " + rtEx.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(rtEx.getMessage()).build();
        }
    }


    @GET
    @Path("/transactions")
    @LoggedIn
    public Response findAccountDevice() {
        try {

            log.info("===============> TRANSACTION LIST");

            return accountService.findAllAccountWithDevices();

        } catch (RuntimeException rtEx) {
            log.error("Error!!! RuntimeException " + rtEx.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(rtEx.getMessage()).build();
        }
    }


    @GET
    @Path("/transactions/status-payment")
    @LoggedIn
    public Response findStatusPayment() {
        try {

            log.info("===============> STATUS PAYMENT");

            List<JSONObject> responseList = new ArrayList<>();
            responseList.add(PaymentStatusEnum.PROCESSED.getLabelResponse());
            responseList.add(PaymentStatusEnum.ERROR.getLabelResponse());
            responseList.add(PaymentStatusEnum.PENDING.getLabelResponse());
            return Response.ok().entity(responseList).build();

        } catch (RuntimeException rtEx) {
            log.error("Error!!! RuntimeException " + rtEx.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(rtEx.getMessage()).build();
        }
    }


    @GET
    @Path("/info")
    public Response getInfoBill() {
        try {

            log.info("===============> INFO BILL ACCOUNT");

            Optional<Account> accountOptional = accountDao.getById(identity.getIdentityDto().getAccountId());

            if (accountOptional.isAbsent()) {
                return Response.ok().build();
            }

            return Response.ok().entity(AccountDto.fromAccountEntity(accountOptional.get())).build();

        } catch (RuntimeException e) {
            log.error("RuntimeException: " + e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @POST
    @LoggedIn
    public Response create(AccountDto accountDto) {
        try {
            log.info("===============> CREATE ACCOUNT");
            log.info("Input: " + accountDto);

            Account account = accountService.createFrom(accountDto, identity);

            Long vector[] = new Long[2];
            vector[0] = account.getId();
            vector[1] = userRepository.findByAccountId(account.getId()).get(0).getId();

            return Response.ok(vector).build();

        } catch (EJBException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error("[ERROR]: " + ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.info("[ERROR]:" + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/info")
    @LoggedIn
    public Response updateInfoBill(AccountDto dto) {
        try {

            log.info("===============> UPDATE INFO BILL ACCOUNT");
            log.info("input: " + dto);

            Optional<Account> accountOptional = accountDao.getById(dto.getId());
            if (accountOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("account not found.").build();
            }
            Account account = AccountDto.accountBill(dto, accountOptional.get());
            account = accountService.update(account);
            return Response.ok().entity(account).build();

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
    @Path("{id}")
    @LoggedIn
    public Response update(@PathParam("id") Long id, AccountDto dto) {
        try {

            log.info("===============> UPDATE ACCOUNT");
            log.info("Input: " + dto);

            Optional<Account> accountOptional = accountDao.getById(id);
            if (accountOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("account not found.").build();
            }

            Optional<User> userOptional = userRepository.getById(dto.getUser().getId());
            if (userOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).entity("user not found.").build();
            }

//            Account account = AccountDto.fromAccountDto(accountOptional.get());
            Account account = AccountDto.from(dto, accountOptional.get());
            User user = AccountDto.from(dto.getUser(), userOptional.get());
            accountService.store(account);
            userService.update(user);
            return Response.ok().build();

        } catch (InvalidInputException ejbEx) {
            InvalidInputException invalidInputException = new InvalidInputException(ejbEx);
            log.error(ejbEx.getMessage(), ejbEx);
            return Response.status(Status.BAD_REQUEST).entity(invalidInputException.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception" + e.getMessage(), e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"account-management"})
    public Response delete(@PathParam("id") Long id) {
        try {

            log.info("===============> DELETE ACCOUNT");
            log.info("id: " + id);

            Optional<Account> accountOptional = accountDao.getById(id);

            if (accountOptional.isAbsent()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            final Account account = accountOptional.get();
            accountService.delete(account);
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