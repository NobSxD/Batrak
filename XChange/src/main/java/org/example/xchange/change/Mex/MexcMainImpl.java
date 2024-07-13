package org.example.xchange.change.Mex;


import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.example.crypto.CryptoUtils;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.mexc.MEXCExchange;
import org.knowm.xchange.mexc.dto.MEXCResult;
import org.knowm.xchange.mexc.dto.account.MEXCBalance;
import org.knowm.xchange.mexc.service.MEXCAccountServiceRaw;

import java.io.IOException;
import java.util.Map;


@Getter
@Setter
public class MexcMainImpl extends BasicChange {
	private final Logger logger = Logger.getLogger(MexcMainImpl.class);

	public MexcMainImpl(NodeUser nodeUser) {
		try {
			CryptoUtils cryptoUtils = new CryptoUtils();
			ExchangeSpecification exSpec = new MEXCExchange().getDefaultExchangeSpecification();
			exSpec.setUserName(nodeUser.getNodeChange().getAccount().getNameAccount());
			exSpec.setApiKey(cryptoUtils.decryptMessage(nodeUser.getNodeChange().getAccount().getPublicApiKey()));
			exSpec.setSecretKey(cryptoUtils.decryptMessage(nodeUser.getNodeChange().getAccount().getSecretApiKey()));
			this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
		} catch (Exception e){
			logger.error(e.getMessage());
		}
	}
	public void accountBalanceInfo(){
		MEXCAccountServiceRaw mar = (MEXCAccountServiceRaw) exchange.getAccountService();

		try {
			MEXCResult<Map<String, MEXCBalance>> walletBalances = mar.getWalletBalances();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public Wallet balances() {
		return null;
	}
}
