package org.example.entity.enams;

public enum SettingUpTrading {


	NamePair("Выбор торговой пары"),

	AmountOrder("Укажете цену ордера"),

	DepthGlass("Глубина размера стакана");



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
