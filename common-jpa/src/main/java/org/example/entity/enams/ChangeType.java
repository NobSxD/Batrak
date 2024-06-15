package org.example.entity.enams;

import lombok.ToString;

@ToString
public enum ChangeType {
    Binance("binance"),
    Baibit("baibit"),
    Mex("mexc");



    private final String value;
    ChangeType(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    public static ChangeType fromValue(String v) {
        for (ChangeType c : ChangeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
