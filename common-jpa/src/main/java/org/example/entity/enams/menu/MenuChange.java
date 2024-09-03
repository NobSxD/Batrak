package org.example.entity.enams.menu;

import org.example.button.ButtonChange;

public enum MenuChange {
	Binance(ButtonChange.binance),
	Bybit(ButtonChange.bybit);
	
	private final String value;
	
	MenuChange(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static MenuChange fromValue(String v) {
		for (MenuChange c : MenuChange.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
