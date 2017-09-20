package ch.swissbytes.module.shared.rest.security.token;


import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.rest.security.dto.JWTClaimSetDto;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

@ApplicationScoped
public class TokenManager implements Serializable {

    @Inject
    protected Logger log;
    @Inject
    RSAKeyManager rsaKeyManager;
    @Inject
    private ExpiredTimeSession expiredTimeSession;

    public String generateToken(JWTClaimSetDto claims) {
        RSAPrivateKey privateKey = (RSAPrivateKey) rsaKeyManager.getPrivateKey();
        JWSSigner signer = new RSASSASigner(privateKey);
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), new Payload(claims.getJwtClaimsSet().toJSONObject()));

        String token = "";
        try {
            jwsObject.sign(signer);
            token = jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
        }

        return token;
    }

    /**
     * If token is valid, return its payload
     *
     * @param token: JWT
     * @return IdentityDto || null
     */
    public JWTClaimSetDto verify(String token, String urlReq) {
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyManager.getPublicKey();
        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        JWTClaimSetDto result = null;
        try {
            JWSObject jwsParsed = JWSObject.parse(token);
            if (jwsParsed.verify(verifier)) {
                JSONObject jsonObject = jwsParsed.getPayload().toJSONObject();
                JWTClaimsSet jwtClaimSet = JWTClaimsSet.parse(jsonObject);

                if (refresh(urlReq)) {
                    expiredTimeSession.setExpirationTime(addExpiredTime());
                }

                jwtClaimSet.setExpirationTime(expiredTimeSession.getExpirationTime());
                JWTClaimSetDto claims = new JWTClaimSetDto(jwtClaimSet);

                result = claims;
            }
        } catch (Exception e) {

        }

        return result == null ? new JWTClaimSetDto() : result;
    }

    private Date addExpiredTime() {
        int addExpTime = KeyAppConfiguration.getInt(ConfigurationKey.ADDITIONAL_EXPIRED_TIME);
        log.info("add expired time " + addExpTime + "min.");
        return new Date(new Date().getTime() + (1000 * 60 * addExpTime));
    }

    private boolean refresh(String urlRequest) {
        String urls = KeyAppConfiguration.getString(ConfigurationKey.URLS_NO_EXTEND_SESSION_TIME);
        for (String url : urls.split(";")) {
            if (urlRequest.equals(url)) {
                return false;
            }
        }
        return true;
    }
}
