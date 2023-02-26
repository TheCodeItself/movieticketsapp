package com.larasierra.movietickets.moviemedia.domain;

public enum MovieMediaRole {
    GALLERY_IMAGE("0"),
    GALLERY_VIDEO("1"),
    POSTER("2"),
    TRAILER("3");

    private final String code;

    MovieMediaRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}