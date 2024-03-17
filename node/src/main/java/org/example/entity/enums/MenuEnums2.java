package org.example.entity.enums;

public enum MenuEnums2 {
	RegisterAccount("регистрация"),
	ChoiceAccount("выбор аккаунта"),
	Trade("Настрайки трейдинга"),
	TradeStart("запуск трейдинга"),
	TradeStop("остановить трейдинг"),
	Cancel("отмена");


	private final String value;
	MenuEnums2(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static MenuEnums2 fromValue(String v) {
		for (MenuEnums2 c : MenuEnums2.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
