package org.example.xchange.Binance;


import lombok.Getter;
import lombok.Setter;
import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;


@Getter
@Setter
public class BinanceMainImpl extends BasicChange{

	public BinanceMainImpl(NodeUser nodeUser) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameAccount());
		exSpec.setApiKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getPublicApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getSecretApiKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}











}
