package org.example.xchange.change.Mex;


import lombok.Getter;
import lombok.Setter;
import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.mexc.MEXCExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Getter
@Setter
public class MexcMainImpl extends BasicChange {
	private final Logger logger = LoggerFactory.getLogger(MexcMainImpl.class);

	public MexcMainImpl(ChangeUser user) {
		try {
			CryptoUtils cryptoUtils = new CryptoUtils();
			ExchangeSpecification exSpec = new MEXCExchange().getDefaultExchangeSpecification();
			exSpec.setUserName(user.getBotName());
			exSpec.setApiKey(cryptoUtils.decryptMessage(user.getApiKey()));
			exSpec.setSecretKey(cryptoUtils.decryptMessage(user.getSecretKey()));
			this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
		} catch (Exception e){
			logger.error(e.getMessage());
		}
	}

	public Wallet balances() {
		return null;
	}
}
