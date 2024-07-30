package org.example.xchange.change.Binance;


import lombok.Getter;
import lombok.Setter;
import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.io.IOException;


@Getter
@Setter
public class BinanceMainImpl extends BasicChange {


	public BinanceMainImpl(ChangeUser user) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(user.getBotName());
		exSpec.setApiKey(cryptoUtils.decryptMessage(user.getApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(user.getSecretKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}


	//TODO сделать вывод информации о балансе
	public Wallet balances() {
		try {
			AccountInfo accountInfo = exchange.getAccountService().getAccountInfo();
			return accountInfo.getWallet();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
