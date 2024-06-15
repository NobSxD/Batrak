package org.example.entity.enams;

public enum Menu3Enums {


	NamePair("Выбор торговой пары"),

	AmountOrder("Укажете цену ордера"),

	DepthGlass("Глубина размера стакана");



	private final String value;
	Menu3Enums(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}

	public static Menu3Enums fromValue(String v) {
		for (Menu3Enums c : Menu3Enums.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		return null;
	}
}
