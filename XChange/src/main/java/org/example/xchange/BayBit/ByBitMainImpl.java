package org.example.xchange.BayBit;

import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.BybitExchange;

public class ByBitMainImpl extends BasicChange {
	public ByBitMainImpl(NodeUser nodeUser) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BybitExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameAccount());
		exSpec.setApiKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getPublicApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getSecretApiKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
}
