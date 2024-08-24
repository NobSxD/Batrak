package org.example.xchange.change.Binance;

import lombok.Getter;
import lombok.Setter;
import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

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


	//TODO сделать вывод информации о балансе
	public BigDecimal balances() {
		try {
			Wallet wallet = exchange.getAccountService().getAccountInfo().getWallet();
			Map<Currency, Balance> balances = wallet.getBalances();
			BigDecimal value = new BigDecimal("0");
			for (Entry<Currency, Balance> entry : balances.entrySet()) {
				Currency curr = entry.getKey();
				Balance bal = entry.getValue();
				if (0 < bal.getAvailable().doubleValue()) {
					value.add(bal.getAvailable());
				}
			}
			
			return value;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
