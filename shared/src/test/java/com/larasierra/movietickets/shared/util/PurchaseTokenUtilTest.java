package com.larasierra.movietickets.shared.util;

import com.larasierra.movietickets.shared.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.springframework.mock.env.MockEnvironment;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PurchaseTokenUtilTest {
    private static final String USERNAME_TEST = "username-test";
    PurchaseTokenUtil purchaseTokenUtil;

    @BeforeAll
    void setup() {
        var environment = new MockEnvironment();
        environment.setProperty("MOVIE_TICKETS_PURCHASE_TOKEN_SECRET", "test_123");
        purchaseTokenUtil = new PurchaseTokenUtil(environment);
    }

    @Test
    void validateToken_whenTokenIsValid() {
        // given
        String timeSortedId = IdUtil.next();

        // when
        String token = purchaseTokenUtil.generateToken(timeSortedId, USERNAME_TEST);

        // then
        assertNotNull(token, "purchase token generated");
        // does not throw
        purchaseTokenUtil.validateToken(USERNAME_TEST, token);
    }

    @Test
    void validateToken_throwsWhenInvalidToken() {
        // given
        var invalidToken = "0000000000000:wjGKHue7ZMxPkwM5EalJzQd3eTKWLygKzS8FuXZl52M=";

        // when
        Executable exec = () ->
                purchaseTokenUtil.validateToken(USERNAME_TEST, invalidToken);

        // then
        assertThrows(UnauthorizedAccessException.class, exec);
    }

    @Test
    void isIdExpired() {
        // given
        var oldTimeSortedId = "0BKTY5SEV5WCG";
        var newTimeSortedId = IdUtil.next();

        // when
        boolean isOldExpired = purchaseTokenUtil.isIdExpired(oldTimeSortedId);
        boolean isNewExpired = purchaseTokenUtil.isIdExpired(newTimeSortedId);

        // then
        assertTrue(isOldExpired, "the old id has expired");
        assertFalse(isNewExpired, "the new id has not expired");
    }
}