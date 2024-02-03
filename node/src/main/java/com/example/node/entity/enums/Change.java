package com.example.node.entity.enums;

public enum Change {
    Binance("binance"),
    Baibit("baibit"),
    Mex("mex");


    private final String value;
    Change(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    public static Change fromValue(String v) {
        for (Change c : Change.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
