package org.example.entity.enams.menu;

import org.example.button.ButtonBasic;

public enum MenuBasic {
	ChoiceAccount(ButtonBasic.choiceAccount),
	DeleteAccount(ButtonBasic.deleteAccount),
	RegisterAccount(ButtonBasic.registerAccount),
	TradeMenu(ButtonBasic.tradeOperation),
	TradeSettings(ButtonBasic.tradeSettings),
	Info_Settings(ButtonBasic.infoSettings);
	
	private final String value;
	
	MenuBasic(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static MenuBasic fromValue(String v) {
		for (MenuBasic c : MenuBasic.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
