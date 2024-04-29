package org.example.xchange.Mex;


import lombok.Getter;
import lombok.Setter;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.mexc.MEXCExchange;

import java.util.List;


@Getter
@Setter
public class MexcMainIml extends BasicChange {
	private Exchange exchange;

	public MexcMainIml(NodeUser nodeUser) {
		ExchangeSpecification exSpec = new MEXCExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameChange());
		exSpec.setApiKey(nodeUser.getAccount().getPublicApiKey());
		exSpec.setSecretKey(nodeUser.getAccount().getSecretApiKey());
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}



	public void tradeStart() {

	}


	public void tradeStop() {

	}


	public List<String> pair() {
		return null;
	}



}
