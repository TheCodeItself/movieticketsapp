package com.larasierra.movietickets.moviemedia.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter
public class MovieMediaRoleConverter implements AttributeConverter<MovieMediaRole, String> {
    @Override
    public String convertToDatabaseColumn(MovieMediaRole category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public MovieMediaRole convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(MovieMediaRole.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
