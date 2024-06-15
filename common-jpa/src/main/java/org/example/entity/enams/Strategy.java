package org.example.entity.enams;

public enum Strategy{
	SlidingProtectiveOrder("Скользящий защитный ордер");

	private final String value;
	Strategy(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static Strategy fromValue(String v) {
		for (Strategy c : Strategy.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
