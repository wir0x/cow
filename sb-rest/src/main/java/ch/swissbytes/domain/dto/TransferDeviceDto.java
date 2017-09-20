package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class TransferDeviceDto implements Serializable {

    private Long deviceId;
    private String emailAccount;

}
