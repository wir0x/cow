package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.PaymentHistoryDao;
import ch.swissbytes.domain.dto.HistoryPaymentDto;
import ch.swissbytes.domain.entities.PaymentHistory;
import ch.swissbytes.module.buho.app.account.repository.AccountRepository;
import ch.swissbytes.module.buho.service.PaymentHistoryService;
import ch.swissbytes.module.shared.rest.security.Identity;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("payments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class PaymentRestEndPoint {

    @Inject
    private Identity identity;
    @Inject
    private Logger log;
    @Inject
    private PaymentHistoryDao paymentHistoryDao;
    @Inject
    private PaymentHistoryService paymentHistoryService;
    @Inject
    private AccountRepository accountDao;


    @GET
    public Response Test() {
        try {
            log.info("************* USER TRANSACTIONS PAYMENT *************");
            Long accountId = identity.getIdentityDto().getAccountId();
            List<PaymentHistory> paymentHistoryList = paymentHistoryDao.findByAccountIdAndStatusEnabled(accountId);

            List<HistoryPaymentDto> historyPaymentDtoList = new ArrayList<>();

            historyPaymentDtoList.addAll(paymentHistoryList.stream()
                    .map(HistoryPaymentDto::fromPaymentHistory)
                    .collect(Collectors.toList()));

            Collections.sort(historyPaymentDtoList, (s1, s2) -> s2.getCreationDate()
                    .compareTo(s1.getCreationDate()));

            log.info("response: " + historyPaymentDtoList.size());
            return Response.ok().entity(historyPaymentDtoList).build();

        } catch (RuntimeException e) {
            log.error("[ERROR] RuntimeException: " + e);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (Exception e) {
            log.error("[ERROR] Exception: " + e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    public Response updatePaymentHistory(HistoryPaymentDto[] historyPaymentDtoList) {
        log.info("******* update payment history  **********");
        log.info("input: " + historyPaymentDtoList.length);
        paymentHistoryService.updateFromDto(historyPaymentDtoList);
        return Response.ok().build();
    }

    @GET
    @Path("{accountId}/{deviceId}/{status}")
    public Response findByTransaction(@PathParam("accountId") Long accountId,
                                      @PathParam("deviceId") Long deviceId,
                                      @PathParam("status") String status) {
        log.info("********* Find Payments by transaction ********");
        log.info(String.format("accountId: %s deviceId: %s status: %s ", accountId, deviceId, status));
        List<PaymentHistory> paymentHistoryList = paymentHistoryService.findTransactions(accountId, deviceId, status);
        Collections.sort(paymentHistoryList, (p1, p2) -> p2.getCreationDate().compareTo(p1.getCreationDate()));
        log.info("response: " + paymentHistoryList.size());
        return Response.ok().entity(paymentHistoryList).build();
    }
}
