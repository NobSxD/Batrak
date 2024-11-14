package org.example.xchange.change.Binance;

import lombok.Getter;
import lombok.Setter;
import org.exampel.crypto.CryptoUtils;
import org.example.xchange.BasicChange;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;

import java.math.BigDecimal;

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
	@Override
	public BigDecimal coinConvertBay(BigDecimal amount, BigDecimal price, int scale) {
		return CurrencyConverter.convertCurrency(price, amount, scale);
	}

	@Override
	public BigDecimal coinConvertSel(BigDecimal amount, BigDecimal price, int scale) {
		return CurrencyConverter.convertCurrency(price, amount, scale);
	}
	
}
