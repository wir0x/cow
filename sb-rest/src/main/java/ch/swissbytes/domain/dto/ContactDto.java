package ch.swissbytes.domain.dto;

import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ContactDto implements Serializable {

    private String from;
    private String title;
    private String message;

    public static ContactDto createNew() {
        ContactDto contactDto = new ContactDto();
        contactDto.from = EntityUtil.DEFAULT_STRING;
        contactDto.title = EntityUtil.DEFAULT_STRING;
        contactDto.message = EntityUtil.DEFAULT_STRING;
        return contactDto;
    }
}
