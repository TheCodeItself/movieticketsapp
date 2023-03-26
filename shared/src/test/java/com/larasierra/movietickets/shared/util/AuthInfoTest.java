package com.larasierra.movietickets.shared.util;

import com.larasierra.movietickets.shared.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AuthInfoTestConf.class)
public class AuthInfoTest {

    @Autowired
    AuthInfo authInfo;
    @Autowired
    JwtEncoder jwtEncoder;

    private static final String USERNAME_TEST = "username-test";

    @WithMockUser(roles = "enduser")
    @DisplayName("hasRole: positive and negative cases")
    @Test
    void hasRole() {
        // given
        var validUserRole = "enduser";
        var invalidUserRole = "internal";

        // when
        boolean hasValidUserRole = authInfo.hasRole(validUserRole);
        boolean hasInvalidUserRole = authInfo.hasRole(invalidUserRole);

        // then
        assertTrue(hasValidUserRole, "User does have the role: enduser");
        assertFalse(hasInvalidUserRole, "User does not have the role: internal");
    }

    @WithMockUser(roles = {"admin", "internal"})
    @DisplayName("hasAnyRole: positive and negative cases")
    @Test
    void hasAnyRole() {
        // given
        var validRoles = new String[] {"admin", "internal"};
        var invalidRoles = new String[] {"enduser", "test"};

        // when
        boolean hasValidRoles = authInfo.hasAnyRole(validRoles);
        boolean hasInvalidRoles = authInfo.hasAnyRole(invalidRoles);

        // then
        assertTrue(hasValidRoles, "User has any valid role");
        assertFalse(hasInvalidRoles, "User does not have any invalid role");
    }

    @Test
    void userId_correctUserId() {
        // given
        String token = generateJwt(Map.of("custom:userId", USERNAME_TEST));

        var request = new MockHttpServletRequest();
        request.addHeader("IdToken", token);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        String userId = authInfo.userId();

        // then
        assertEquals(USERNAME_TEST, userId, "correct user id");
    }

    @Test
    void userId_throwsOnMissingIdTokenHeader() {
        // given missing IdToken header
        var request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        Executable exec = () -> authInfo.userId();

        // then
        assertThrows(UnauthorizedAccessException.class, exec);
    }

    @Test
    void userId_throwsOnMissingCustomUserIdClaim() {
        // given missing userId claim
        var request = new MockHttpServletRequest();
        request.addHeader("IdToken", generateJwt(Map.of()));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        Executable exec = () -> authInfo.userId();

        // then
        assertThrows(UnauthorizedAccessException.class, exec);
    }

    private String generateJwt(Map<String, String> claims) {
        var jwtClaimsBuilder = JwtClaimsSet.builder();

        claims.forEach(jwtClaimsBuilder::claim);

        JwtClaimsSet jwtClaims = jwtClaimsBuilder
                .expiresAt(Instant.now().plusSeconds(60))
                .issuedAt(Instant.now())
                .build();

        var header = JwsHeader.with(() -> "HS256").build();

        var params = JwtEncoderParameters.from(header, jwtClaims);

        Jwt jwt = jwtEncoder.encode(params);
        return jwt.getTokenValue();
    }
}
