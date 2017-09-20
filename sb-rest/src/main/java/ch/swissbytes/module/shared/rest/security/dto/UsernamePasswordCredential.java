package ch.swissbytes.module.shared.rest.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.enterprise.context.RequestScoped;

@RequestScoped
@Getter
@Setter
@ToString
public class UsernamePasswordCredential {
    private String userId;
    private String password;
    private String uniqueId;
    private String gcmToken;


    public UsernamePasswordCredential() {
    }

    public UsernamePasswordCredential(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
