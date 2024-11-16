package org.example.xchange.change.BayBit;

import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bybit.BybitExchange;

import java.math.BigDecimal;

public class ByBitMainImpl extends BasicChange {
	public ByBitMainImpl(ChangeUser user) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		ExchangeSpecification exSpec = new BybitExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(user.getBotName());
		exSpec.setApiKey(cryptoUtils.decryptMessage(user.getApiKey()));
		exSpec.setSecretKey(cryptoUtils.decryptMessage(user.getSecretKey()));
		this.exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}

	@Override
	public BigDecimal coinConvertBay(BigDecimal amount, BigDecimal price, int scale) {
		return CurrencyConverter.convertCurrency(price, amount, scale);
	}

	@Override
	public BigDecimal coinConvertSel(BigDecimal amount, BigDecimal price, int scale) {
		return CurrencyConverter.convertCurrency(price, amount, scale);
	}
}
