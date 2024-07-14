package org.example.xchange.change.Binance;


import lombok.Getter;
import lombok.Setter;
import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.io.IOException;


@Getter
@Setter
public class BinanceMainImpl extends BasicChange {


	public BinanceMainImpl(NodeUser nodeUser) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameAccount());
		System.out.println(nodeUser.getAccount().getNameAccount());
		exSpec.setApiKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getPublicApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getSecretApiKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}


	@Override
	//TODO сделать вывод информации о балансе
	public Wallet balances() {
		try {
			AccountInfo accountInfo = exchange.getAccountService().getAccountInfo();
		//	Wallet wallet = BinanceAdapters.adaptBinanceSpotWallet(accountInfo.getWallet());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}
}
