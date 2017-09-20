package ch.swissbytes.module.buho.rest.endpoint;

import ch.swissbytes.domain.dao.SmsControlDao;
import ch.swissbytes.domain.dto.SmsControlDto;
import ch.swissbytes.domain.dto.StateAccountDto;
import ch.swissbytes.domain.dto.SubscriptionDto;
import ch.swissbytes.domain.entities.SmsControl;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.device.repository.DeviceRepository;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import ch.swissbytes.module.buho.app.subscription.repository.SubscriptionRepository;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
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
import java.util.ArrayList;
import java.util.List;


@Path("state-account")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})

public class StateAccountRestEndPoint {

    @Inject
    private Logger log;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private SmsControlDao smsControlDao;

    @Inject
    private Identity identity;

    @GET
    @Path("/list")
    @LoggedIn
    public Response findAllStateAccountByCompanyId() {
        try {

            List<Device> deviceList = deviceRepository.findByAccountId(identity.getIdentityDto().getAccountId());
            List<StateAccountDto> subscriptionDtoList = findStateAccount(deviceList);

            return Response.ok().entity(subscriptionDtoList).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private List<StateAccountDto> findStateAccount(List<Device> deviceList) {
        log.info("findStateAccount " + deviceList);

        List<StateAccountDto> stateAccountDtoList = new ArrayList<>();

        for (Device device : deviceList) {
            if (subscriptionRepository.getActiveByDeviceId(device.getId()).isPresent()) {
                List<Subscription> subscriptionList = subscriptionRepository.findByDeviceId(device.getId());
                StateAccountDto stateAccountDto = StateAccountDto.fromEntity(subscriptionList.get(subscriptionList.size() - 1));
                stateAccountDto.setSubscriptionDtoList(findSubscriptionDtoList(device));

                stateAccountDtoList.add(stateAccountDto);
            }
        }

        return stateAccountDtoList;
    }

    private List<SmsControlDto> smsControlDtos(Subscription subscription) {
        List<SmsControl> smsControlList = smsControlDao.findByDeviceId(subscription.getDevice().getId());
        List<SmsControlDto> smsControlDtoList = new ArrayList<>();

        int limitMonthToShow = KeyAppConfiguration.getInt(ConfigurationKey.MONTH_NUMBER_TO_SHOW);
        if (smsControlList.size() < limitMonthToShow) {
            limitMonthToShow = smsControlList.size();
        }

        for (int i = limitMonthToShow; i > 0; i--) {
            smsControlDtoList.add(SmsControlDto.fromSmsControl(smsControlList.get(i - 1)));
        }

        return smsControlDtoList;
    }

    private boolean isMonthWithinSubscriptionDates(Subscription subscription, SmsControl smsControl) {
        if (smsControl.getMonthYear().getTime() <= subscription.getStartDate().getTime()) {
            return true;
        } else {
            return false;
        }
    }

    private List<SubscriptionDto> findSubscriptionDtoList(Device device) {
        List<SubscriptionDto> subscriptionDtoList = new ArrayList<>();
        List<Subscription> subscriptionList = subscriptionRepository.findByDeviceId(device.getId());
        log.info("subscription size " + subscriptionList.size());

        int limitMonthToShow = monthNumberToShow(subscriptionList.size());
        for (int i = 0; i < limitMonthToShow; i++) {
            SubscriptionDto subscriptionDto = SubscriptionDto.fromSubscriptionEntity(subscriptionList.get(i), device, device.getAccount());
            subscriptionDto.setSmsControlDtoList(smsControlDtos(subscriptionList.get(i)));
            subscriptionDtoList.add(subscriptionDto);
        }

        return subscriptionDtoList;
    }

    private int monthNumberToShow(int subscriptionSize) {
        int monthNumber = KeyAppConfiguration.getInt(ConfigurationKey.MONTH_NUMBER_TO_SHOW);
        if (subscriptionSize > monthNumber) {
            return monthNumber;
        } else {
            return subscriptionSize;
        }
    }

}
