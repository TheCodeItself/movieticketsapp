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

import java.util.Objects;

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

    /**
     * Obtains the organizerId from the IdToken header
     * @return the organizer extracted from the IdToken header
     * @throws UnauthorizedAccessException if no IdToken header is found in the request, or if no organizerId claim is found in the token
     */
    public String  organizerId() {
        return getRequiredCustomAttribute("custom:organizerId");
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

    /**
     * Valid that the organizerId received in a request belong to the user. This method use the IdToken Header for the test
     * @param organizerId the organizerId to test against the IdToken
     * @throws UnauthorizedAccessException if the user is unauthorized for the given organizerId
     * @throws IllegalStateException If the validation cannot be performed
     */
    public void validOrganizer(String organizerId) {
        if (organizerId == null || organizerId.isBlank()) {
            throw new UnauthorizedAccessException();
        }

        String idToken = getRequestHeader("IdToken");

        if (idToken == null) {
            throw new UnauthorizedAccessException();
        }

        String organizerIdClaim = getTokenIdClaim(idToken, "custom:organizerId");

        if (!Objects.equals(organizerId, organizerIdClaim)) {
            throw new UnauthorizedAccessException();
        }
    }

    /**
     * Determine if the given organizerId is the same organizerId that is present in the IdToken
     * @param organizerId the organizerId to test against the IdToken
     * @param idToken the IdToken to use for the test
     * @return true if the organizerId is the same as the one present in the IdToken and false otherwise
     */
    public boolean isValidOrganizer(String organizerId, String idToken) {
        if(organizerId == null || organizerId.isBlank()) {
            return false;
        }

        if (idToken == null) {
            return false;
        }

        String organizerIdClaim = getTokenIdClaim(idToken, "custom:organizerId");

        return Objects.equals(organizerId, organizerIdClaim);
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
