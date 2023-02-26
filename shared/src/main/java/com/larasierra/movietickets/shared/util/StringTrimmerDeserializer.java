package com.larasierra.movietickets.shared.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StringTrimmerDeserializer extends StringDeserializer {

    protected StringTrimmerDeserializer() {
        super();
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = super.deserialize(p, ctx);

        if (value == null) {
            return null;
        }

        value = value.trim();
        if (value.length() == 0) {
            return null;
        }

        return value;
    }
}