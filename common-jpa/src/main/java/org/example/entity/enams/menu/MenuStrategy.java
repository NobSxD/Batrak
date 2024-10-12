package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuStrategy {

	GridTrading(ButtonLabelManager.gridTrading);

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
