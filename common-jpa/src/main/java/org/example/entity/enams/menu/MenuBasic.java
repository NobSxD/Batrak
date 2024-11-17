package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuBasic {
	ChoiceAccount(ButtonLabelManager.choiceAccount),
	DeleteAccount(ButtonLabelManager.deleteAccount),
	RegisterAccount(ButtonLabelManager.registerAccount),
	TradeMenu(ButtonLabelManager.tradeOperation),
	TradeSettings(ButtonLabelManager.tradeSettings),
	TradeStatistics(ButtonLabelManager.statistics),
	Info_Settings(ButtonLabelManager.infoSettings);
	
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
