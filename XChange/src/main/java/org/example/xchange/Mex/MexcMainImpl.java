package org.example.xchange.Mex;


import lombok.Getter;
import lombok.Setter;
import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.mexc.MEXCExchange;


@Getter
@Setter
public class MexcMainImpl extends BasicChange {

	public MexcMainImpl(NodeUser nodeUser) {

		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new MEXCExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(nodeUser.getAccount().getNameAccount());
		exSpec.setApiKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getPublicApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(nodeUser.getAccount().getSecretApiKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}

}
