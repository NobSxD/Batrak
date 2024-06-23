package org.example.entity.enams;

public enum SettingUpTrading {


	NamePair("Выбор торговой пары"),

	AmountOrder("Укажите цену ордера"),

	DepthGlass("Глубина размера стакана"),
	SelectStrategy("Выбор стратегии"),
	MainMenu("В главное мею");



	private final String value;
	SettingUpTrading(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static SettingUpTrading fromValue(String v) {
		for (SettingUpTrading c : SettingUpTrading.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
