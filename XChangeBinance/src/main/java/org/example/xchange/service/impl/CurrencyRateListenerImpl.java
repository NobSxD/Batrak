package org.example.xchange.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.xchange.service.CurrencyRateListener;
import org.example.xchange.service.SubscriptionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.RATE;

@Service
@RequiredArgsConstructor
public class CurrencyRateListenerImpl implements CurrencyRateListener {
	private final SubscriptionService subscriptionService;
	@Override
	@RabbitListener(queues = RATE)
	public void receiveMessage(String message) {
		System.out.println("Received <" + message + ">");
	}
}
