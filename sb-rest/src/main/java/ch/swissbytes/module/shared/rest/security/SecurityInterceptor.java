package ch.swissbytes.module.shared.rest.security;

import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.rest.security.annotations.LoggedIn;
import ch.swissbytes.module.shared.rest.security.dto.IdentityDto;
import ch.swissbytes.module.shared.rest.security.dto.JWTClaimSetDto;
import ch.swissbytes.module.shared.rest.security.token.TokenManager;
import ch.swissbytes.module.shared.utils.StringUtil;
import org.jboss.logging.Logger;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    private final static TimerThreadLocal timer = new TimerThreadLocal();

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String OPERATING_SYSTEM_PROPERTY = "OS";
    private static final String AUTHENTICATION_SCHEME = "Token";

    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<Object>());
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("{\"msg\": \"Access denied for this resource\"}", 401, new Headers<Object>());
    private static final ServerResponse EXPIRED_SESSION = new ServerResponse("{\"msg\": \"Your session has expired\"}", 440, new Headers<Object>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<Object>());
    @Inject
    protected Logger log;
    @Inject
    TokenManager tokenManager;
    @Inject
    Identity identity;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        timer.start();

        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) containerRequestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();

        JWTClaimSetDto claims = getClaim(containerRequestContext);
        IdentityDto identityDto = claims.getAccount();
        String os = extractAppKeyFromRequest(containerRequestContext);

        identity.setIdentityDto(identityDto);
        log.info("Expired time => [" + claims.getExpiredTime() + "]");

        if (identityDto != null) {
            log.info("It's already authenticated: " + identityDto.getUserName());
        } else {
            log.info("Is no authenticated");
        }

        if (validateExpiration(claims, os, identityDto)) {
            log.info("EXPIRED SESSION");
            containerRequestContext.abortWith(EXPIRED_SESSION);
            return;
        }

        if (method.isAnnotationPresent(LoggedIn.class)) {
            log.info("Method is annotated with LoggedIn annotation");

            if (identityDto == null) {
                containerRequestContext.abortWith(ACCESS_DENIED);
                return;
            }

            if (validateExpiration(claims, os, identityDto)) {
                log.info("EXPIRED SESSION");
                containerRequestContext.abortWith(EXPIRED_SESSION);
                return;
            }
        }
        log.info("Method: " + containerRequestContext.getMethod());
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            log.info("Method is annotated with RolesAllowed annotation");

            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));

            if (identityDto == null) {
                containerRequestContext.abortWith(ACCESS_DENIED);
                return;
            }

            if (validateExpiration(claims, os, identityDto)) {
                log.info("EXPIRED SESSION");
                containerRequestContext.abortWith(EXPIRED_SESSION);
                return;
            }

            boolean hasRole = false;
            for (String role : rolesSet) {
                if (identityDto.getPermission().contains(role)) {
                    hasRole = true;
                }
            }

            if (!hasRole) {
                containerRequestContext.abortWith(ACCESS_DENIED);
                return;
            }
        }
    }

    /**
     * @param containerRequestContext
     * @return
     */
    private JWTClaimSetDto getClaim(ContainerRequestContext containerRequestContext) {
        String token = extractTokenFromRequest(containerRequestContext);
        String urlReq = containerRequestContext.getUriInfo().getPath();
        JWTClaimSetDto claims = tokenManager.verify(token, urlReq);

        return claims;
    }

    private String extractTokenFromRequest(ContainerRequestContext containerRequestContext) {

        String token = null;

        // Get request headers
        final MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();

        // Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if (authorization != null && !authorization.isEmpty()) {
            token = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
        }

        return token;
    }

    private String extractAppKeyFromRequest(ContainerRequestContext containerRequestContext) {
        // Get request headers
        final MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();

        // Fetch OS header
        final List<String> header = headers.get(OPERATING_SYSTEM_PROPERTY);

        return header != null ? header.toString() : "";
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) containerRequestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        try {
            Method method = methodInvoker.getMethod();
        } catch (Exception ex) {

        }

        Double durationSec = (double) (timer.stop() / 1000);
        log.info("Rest method " + containerRequestContext.getUriInfo().getPath() + " Elapse Time: [" + timer.stop() + "] ms");

    }

    private boolean isAppViewer(String os) {
        if (StringUtil.isEmpty(os)) {
            return false;
        }

        String mobileOsKey = KeyAppConfiguration.getString(ConfigurationKey.MOBILE_OS_KEY);

        for (String osKey : mobileOsKey.split(";")) {
            log.info("osKey => " + osKey);
            if (os.equals(osKey.trim())) {
                return true;
            }
        }
        return false;
    }

    boolean validateExpiration(JWTClaimSetDto claims, String os, IdentityDto identityDto) {
        return claims.isExpired() && !isAppViewer(os) && !identityDto.getIsWithoutExpiration();
    }

    private final static class TimerThreadLocal extends ThreadLocal<Long> {
        public Long start() {
            Long value = System.currentTimeMillis();
            this.set(value);
            return value;
        }

        public Long stop() {
            return System.currentTimeMillis() - get();
        }

        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    }
}
