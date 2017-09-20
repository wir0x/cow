package ch.swissbytes.module.shared.rest.security;

import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import net.minidev.json.JSONObject;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class Identity {

    IdentityDto identityDto;

    public IdentityDto getIdentityDto() {
        return identityDto;
    }

    public void setIdentityDto(IdentityDto identityDto) {
        this.identityDto = identityDto;
    }

    public JSONObject getAccountJSON() {
        return identityDto != null ? identityDto.getJsonObject() : null;
    }

    public boolean isLoggedIn() {
        return identityDto != null;
    }

    public boolean hasRole(String role) {
        return identityDto.getRoles().contains(role);
    }


}
