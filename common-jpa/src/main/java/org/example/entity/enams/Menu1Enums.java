package org.example.entity.enams;

import lombok.ToString;

@ToString
public enum Menu1Enums {
    Binance("binance"),
    Baibit("baibit"),
    Mex("mexc");



    private final String value;
    Menu1Enums(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    public static Menu1Enums fromValue(String v) {
        for (Menu1Enums c : Menu1Enums.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
