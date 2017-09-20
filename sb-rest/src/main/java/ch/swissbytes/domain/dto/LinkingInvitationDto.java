package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LinkingInvitationDto implements Serializable {

    private String email;
    private String imei;

}
