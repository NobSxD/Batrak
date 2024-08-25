package org.example.xchange.change.Binance;

import org.example.xchange.DTO.ChangeUser;

import org.exampel.crypto.CryptoUtils;
import org.junit.jupiter.api.BeforeEach;

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
	
	

}