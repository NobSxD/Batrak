package org.example.xchange.service;

import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;

public interface SubscriptionService {
	void addSubscription(String userId, CurrencyPair currencyPair, BigDecimal targetRate);
}
