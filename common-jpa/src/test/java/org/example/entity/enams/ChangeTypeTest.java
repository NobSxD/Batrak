package org.example.entity.enams;

import org.example.entity.collect.ChangeType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeTypeTest {

	@Test
	void typChange(){
		ChangeType changeType = ChangeType.fromValue("binance");
		assertEquals(changeType, ChangeType.Binance);
		

		changeType = ChangeType.fromValue("bybit");
		assertEquals(changeType, ChangeType.Bybit);
	}
}