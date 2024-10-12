package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuOperation {
	TradeStart(ButtonLabelManager.tradeStart),
	TradeStop(ButtonLabelManager.tradeStop),
	TradeCancel(ButtonLabelManager.tradeCancel),
	MainMenu(ButtonLabelManager.mainMenu);
	
	private final String value;
	MenuOperation(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}
	
	public static MenuOperation fromValue(String v) {
		for (MenuOperation c : MenuOperation.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
