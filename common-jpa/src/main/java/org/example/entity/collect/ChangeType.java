package org.example.entity.collect;

import org.example.button.ButtonLabelManager;

public enum ChangeType {
	Binance(ButtonLabelManager.binance),
	Bybit(ButtonLabelManager.bybit);
	
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
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		return null;
	}
}
