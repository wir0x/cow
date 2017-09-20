package ch.swissbytes.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class AddSmartphoneDto implements Serializable {

    private String name;
    private String generatedId;

}
