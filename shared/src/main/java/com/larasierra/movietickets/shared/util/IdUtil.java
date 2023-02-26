package com.larasierra.movietickets.shared.util;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class IdUtil {
    private static final TsidFactory factory;

    static {
        factory = TsidFactory.builder()
                .withRandomFunction(length -> {
                    final byte[] bytes = new byte[length];
                    ThreadLocalRandom.current().nextBytes(bytes);
                    return bytes;
                })
                .build();
    }

    public static String next() {
        return factory.create().toString();
    }

    public static Instant extractInstant(String id) {
        return Tsid.from(id).getInstant();
    }
}
