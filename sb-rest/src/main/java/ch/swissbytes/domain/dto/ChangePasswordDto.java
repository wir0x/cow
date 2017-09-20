package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChangePasswordDto implements Serializable {

    private Long userId;
    private String newPassword;
    private String confirmPassword;

}
