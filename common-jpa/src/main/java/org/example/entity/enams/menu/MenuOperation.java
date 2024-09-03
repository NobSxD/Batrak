package org.example.entity.enams.menu;

import org.example.button.ButtonMainMenu;
import org.example.button.ButtonOperation;

public enum MenuOperation {
	TradeStart(ButtonOperation.tradeStart),
	TradeStop(ButtonOperation.tradeStop),
	TradeCancel(ButtonOperation.tradeCancel),
	MainMenu(ButtonMainMenu.mainMenu);
	
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
