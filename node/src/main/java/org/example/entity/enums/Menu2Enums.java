package org.example.entity.enums;

public enum Menu2Enums {
	Cancel("отмена"),
	ChoiceAccount("выбор аккаунта"),
	RegisterAccount("регистрация"),

	Trade("Настрайки трейдинга"),
	TradeStart("запуск трейдинга"),
	TradeStop("остановить трейдинг");



	private final String value;
	Menu2Enums(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static Menu2Enums fromValue(String v) {
		for (Menu2Enums c : Menu2Enums.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
