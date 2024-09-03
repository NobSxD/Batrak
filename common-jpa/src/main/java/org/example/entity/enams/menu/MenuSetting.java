package org.example.entity.enams.menu;

import org.example.button.ButtonMainMenu;
import org.example.button.ButtonSetting;

public enum MenuSetting {
	NamePair(ButtonSetting.namePair),
	AmountOrder(ButtonSetting.amountOrder),
	DepthGlass(ButtonSetting.depthGlass),
	SelectStrategy(ButtonSetting.selectStrategy),
	MainMenu(ButtonMainMenu.mainMenu);
	
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
