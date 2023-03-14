package com.larasierra.movietickets.shared.util;

import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.UnauthorizedAccessException;
import org.springframework.core.env.Environment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

public class PurchaseTokenUtil {

    private final String purchaseTokenSecret;
    public PurchaseTokenUtil(Environment environment) {
        purchaseTokenSecret = environment.getProperty("MOVIE_TICKETS_PURCHASE_TOKEN_SECRET");
    }

    public String generateToken(String timeSortedId, String userId) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AppInternalErrorException();
        }

        String plainText = timeSortedId + ":" + userId + ":" + purchaseTokenSecret;

        byte[] hashBytes = messageDigest.digest(plainText.getBytes());

        return timeSortedId + ":" + Base64.getEncoder().encodeToString(hashBytes);
    }

    public void validateToken(String userId, String purchaseToken) {
        String[] split = purchaseToken.split(":");
        String providedTimeSortedId = split[0];

        // only the user who requested the token can use it to add a seat to the cart or place an order
        String calculatedToken = generateToken(providedTimeSortedId, userId);

        if (!calculatedToken.equals(purchaseToken)) {
            throw new UnauthorizedAccessException();
        }
    }

    public boolean isTokenExpired(String purchaseToken) {
       String timeSortedId = purchaseToken.substring(0, 13);
       return isIdExpired(timeSortedId);
    }

    public boolean isIdExpired(String timeSortedId) {
        Instant instant = IdUtil.extractInstant(timeSortedId);
        Instant expiration = instant.plusSeconds(60 * 5);

        return expiration.isBefore(Instant.now());
    }

}
