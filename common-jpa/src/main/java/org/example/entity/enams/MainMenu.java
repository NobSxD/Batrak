package org.example.entity.enams;

public enum MainMenu {

	ChoiceAccount("выбор аккаунта"),
	RegisterAccount("регистрация"),

	Trade("Настрайки трейдинга"),
	TradeStart("запуск трейдинга"),
	TradeStop("остановить трейдинг");



	private final String value;
	MainMenu(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static MainMenu fromValue(String v) {
		for (MainMenu c : MainMenu.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
