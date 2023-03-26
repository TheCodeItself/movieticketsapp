package com.larasierra.movietickets.shared.util;

import com.larasierra.movietickets.shared.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthInfo {

    private final JwtDecoder decoder;

    public AuthInfo(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    public boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        var role = new SimpleGrantedAuthority("ROLE_" + roleName);

        return auth.getAuthorities().contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        for (String roleName : roles) {
            var role = new SimpleGrantedAuthority("ROLE_" + roleName);
            return auth.getAuthorities().contains(role);
        }
        return false;
    }

    /**
     * Extract the application user id from the IdToken header
     * @return the application user id extracted from the IdToken header
     * @throws UnauthorizedAccessException if no userId is found
     */
    public String userId() {
        return getRequiredCustomAttribute("custom:userId");
    }

    /**
     * @return returns the oauth2 provider user id from the {@link SecurityContextHolder}
     */
    public String getOauthProviderUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            throw new UnauthorizedAccessException();
        }

        if (!(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedAccessException();
        }

        return jwt.getSubject();
    }

    public String getRequiredCustomAttribute(String attributeName) {
        String idToken = getRequestHeader("IdToken");

        if (idToken == null) {
            throw new UnauthorizedAccessException();
        }

        String attribute = getTokenIdClaim(idToken, attributeName);

        if (attribute == null || attribute.isBlank()) {
            throw new UnauthorizedAccessException();
        }

        return attribute;
    }

    private String getTokenIdClaim(String idToken, String claimName) {
        try {
            Jwt jwt = decoder.decode(idToken);
            return jwt.getClaimAsString(claimName);
        } catch(JwtException e) {
            throw new IllegalStateException();
        }
    }

    private String getRequestHeader(String headerName) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getHeader(headerName);
    }
}
