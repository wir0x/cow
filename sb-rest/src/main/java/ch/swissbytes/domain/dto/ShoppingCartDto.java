package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.entities.ServicePlan;
import ch.swissbytes.module.buho.app.device.model.Device;
import ch.swissbytes.module.buho.app.subscription.model.Subscription;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ShoppingCartDto implements Serializable {

    private Long id;
    private DeviceDto device;
    private ServicePlan servicePlan;

    public static ShoppingCartDto createNew() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.device = null;
        shoppingCartDto.servicePlan = null;
        return shoppingCartDto;
    }

    public static ShoppingCartDto from(Device device, ServicePlan servicePlan, Long id) {
        ShoppingCartDto shoppingCartDto = createNew();
        shoppingCartDto.id = id;
        shoppingCartDto.device = DeviceDto.from(device);
        shoppingCartDto.servicePlan = servicePlan;
        return shoppingCartDto;
    }

    public static ShoppingCartDto from(Subscription subscription) {
        return from(subscription.getDevice(), subscription.getServicePlan(), subscription.getId());
    }

}
