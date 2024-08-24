package org.example.xchange.change.Binance;

import org.exampel.crypto.CryptoUtils;
import org.example.xchange.DTO.ChangeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

class BinanceMainImplTest {
	
	ChangeUser changeUser;
	BinanceMainImpl binanceMain;
	@BeforeEach
	public void setUp() {
		CryptoUtils cryptoUtils = new CryptoUtils();
		changeUser = ChangeUser.builder()
							   .botName("Bot")
							   .apiKey(cryptoUtils.encryptMessage(System.getenv("pKey")))
							   .secretKey(cryptoUtils.encryptMessage(System.getenv("sKey")))
							   .build();
		
		binanceMain = new BinanceMainImpl(changeUser);
	}
	
	
	@Test
	public void testBalances(){
		BigDecimal balances = binanceMain.balances();
		System.out.println("баланс: " + balances);
	}
	
	@Test
	public void testBalancesThrowsIOException() throws IOException {
	
	}

}