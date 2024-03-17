package com.example.account;

import org.example.entity.NodeUser;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;

public class AccountXchangeImpl implements  AccountXchange {
	@Override
	public Exchange instance(NodeUser nodeUser, BaseExchange baseExchange) {
		ExchangeSpecification exSpec = baseExchange.getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccountName());
		exSpec.setApiKey(nodeUser.getPublicApi());
		exSpec.setSecretKey(nodeUser.getSecretApi());
		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
}
