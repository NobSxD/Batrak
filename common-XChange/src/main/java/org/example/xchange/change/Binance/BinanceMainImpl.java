package org.example.xchange.change.Binance;

import lombok.Getter;
import lombok.Setter;
import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;

@Getter
@Setter
public class BinanceMainImpl extends BasicChange {


	public BinanceMainImpl(ChangeUser user) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(user.getBotName());
		exSpec.setApiKey(cryptoUtils.decryptMessage(user.getApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(user.getSecretKey()));
		exSpec.setShouldLoadRemoteMetaData(true);
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
	
}
