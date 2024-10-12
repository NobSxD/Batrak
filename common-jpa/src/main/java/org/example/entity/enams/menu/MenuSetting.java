package org.example.entity.enams.menu;

import org.example.button.ButtonLabelManager;

public enum MenuSetting{
	NamePair(ButtonLabelManager.namePair),
	AmountOrder(ButtonLabelManager.amountOrder),
	Step(ButtonLabelManager.step),
	SelectStrategy(ButtonLabelManager.selectStrategy),
	DemoTrade(ButtonLabelManager.enableDemoTrading),
	MainMenu(ButtonLabelManager.mainMenu);
	
	private final String value;
	
	MenuSetting(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static MenuSetting fromValue(String v) {
		for (MenuSetting c : MenuSetting.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
