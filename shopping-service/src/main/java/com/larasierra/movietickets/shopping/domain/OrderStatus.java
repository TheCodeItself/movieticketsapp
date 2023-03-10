package com.larasierra.movietickets.shopping.domain;

public enum OrderStatus {
    PENDING("pend"),
    PROCESSING("proc"),
    SUCCEEDED("succ"),
    FAIL("fail"),
    CANCEL("cncl");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}