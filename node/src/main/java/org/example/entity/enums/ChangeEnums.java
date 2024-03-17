package org.example.entity.enums;

import lombok.ToString;

@ToString
public enum ChangeEnums {
    Binance("binance"),
    Baibit("baibit"),
    Mex("mexc");



    private final String value;
    ChangeEnums(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    public static ChangeEnums fromValue(String v) {
        for (ChangeEnums c : ChangeEnums.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
