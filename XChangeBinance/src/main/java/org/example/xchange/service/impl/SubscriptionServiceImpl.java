package org.example.xchange.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.castom.UserSubscription;
import org.example.xchange.service.SubscriptionService;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
	private final Map<String, Set<UserSubscription>> userSubscriptions = new ConcurrentHashMap<>(); // Хранение подписок пользователей


	public void addSubscription(String userId, CurrencyPair currencyPair, BigDecimal targetRate) {
		userSubscriptions.putIfAbsent(userId, ConcurrentHashMap.newKeySet());
		UserSubscription userSubscription = UserSubscription.builder()
				.userId(userId)
				.currencyPair(currencyPair.toString())
				.targetRate(targetRate)
				.build();
		userSubscriptions.get(userId).add(userSubscription);
	}

	public Set<UserSubscription> getSubscriptions(String userId) {
		return userSubscriptions.get(userId);
	}
}
