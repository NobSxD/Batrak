package org.example.entity.enams;

public enum StrategyEnams {
	SlidingProtectiveOrder("Скользящий защитный ордер"),
	PredictionsOfEvents("Предскозания событий");

	private final String value;
	StrategyEnams(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static StrategyEnams fromValue(String v) {
		for (StrategyEnams c : StrategyEnams.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
