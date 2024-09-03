package org.example.entity.enams.menu;

import org.example.button.ButtonStrategy;

public enum MenuStrategy {
	SlidingProtectiveOrder(ButtonStrategy.slidingProtectiveOrder),
	PredictionsOfEvents(ButtonStrategy.predictionsOfEvents);

	private final String value;
	MenuStrategy(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static MenuStrategy fromValue(String v) {
		for (MenuStrategy c : MenuStrategy.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
