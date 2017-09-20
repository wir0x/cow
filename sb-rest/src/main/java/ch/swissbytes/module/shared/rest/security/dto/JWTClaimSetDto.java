package ch.swissbytes.module.shared.rest.security.dto;

import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class JWTClaimSetDto {

    private static final int timeSession = KeyAppConfiguration.getInt(ConfigurationKey.DEFAULT_EXPIRED_TIME);
    private JWTClaimsSet jwtClaimsSet;
    private IdentityDto identityDto;

    public JWTClaimSetDto() {
        jwtClaimsSet = new JWTClaimsSet();
        init();
        jwtClaimsSet.setCustomClaim("account", null);
    }

    public JWTClaimSetDto(IdentityDto account) {
        jwtClaimsSet = new JWTClaimsSet();
        init();
        jwtClaimsSet.setCustomClaim("account", account.getJsonObject());
    }

    public JWTClaimSetDto(JWTClaimsSet jwtClaimsSet) {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    private void init() {
        jwtClaimsSet.setIssuer("web");
        jwtClaimsSet.setExpirationTime(new Date(new Date().getTime() + 1000 * 60 * timeSession)); //5 min
        jwtClaimsSet.setNotBeforeTime(new Date());
        jwtClaimsSet.setIssueTime(new Date());
        jwtClaimsSet.setJWTID(UUID.randomUUID().toString());
    }

    public JWTClaimsSet getJwtClaimsSet() {
        return jwtClaimsSet;
    }


    public boolean isExpired() {
        return Calendar.getInstance().getTime().after(jwtClaimsSet.getExpirationTime());
    }

    public IdentityDto getAccount() {
        if (identityDto == null && jwtClaimsSet.getCustomClaim("account") != null) {
            JSONObject accountJson = (JSONObject) jwtClaimsSet.getCustomClaim("account");
            identityDto = new IdentityDto(accountJson);
        }
        return identityDto;
    }

    public void setAccount(IdentityDto account) {
        jwtClaimsSet.setCustomClaim("account", account);
    }

    public Date getExpiredTime() {
        return jwtClaimsSet.getExpirationTime();
    }
}
