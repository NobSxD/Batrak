package org.example.entity.enams;

import org.example.entity.enams.menu.MenuChange;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuChangeTest {

	@Test
	void typChange(){
		MenuChange menuChange = MenuChange.fromValue("binance");
		assertEquals(menuChange, MenuChange.Binance);
		

		menuChange = MenuChange.fromValue("bybit");
		assertEquals(menuChange, MenuChange.Bybit);
	}
}