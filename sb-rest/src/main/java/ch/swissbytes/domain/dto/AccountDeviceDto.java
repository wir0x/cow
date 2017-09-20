package ch.swissbytes.domain.dto;

import ch.swissbytes.module.buho.app.account.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AccountDeviceDto implements Serializable {

    private Long accountId;
    private String accountName;
    private List<DeviceDto> deviceList;

    public static AccountDeviceDto fromAccountAndDeviceList(Account account, List<DeviceDto> deviceDtoList) {
        AccountDeviceDto accountDeviceDto = new AccountDeviceDto();
        accountDeviceDto.accountId = account.getId();
        accountDeviceDto.accountName = account.getName();
        accountDeviceDto.deviceList = deviceDtoList;
        return accountDeviceDto;
    }
}
