package org.example.service;

import org.example.entity.NodeUser;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static org.example.model.RabbitQueue.TRADE_CANCEL_ORDER;
import static org.example.model.RabbitQueue.TRADE_MESSAGE;

public interface MainServiceTradeBot {
	
	@RabbitListener(queues =  TRADE_MESSAGE)
	void startORStopTrade(NodeUser nodeUser);
	
	@RabbitListener(queues = TRADE_CANCEL_ORDER)
	void cancelOrder(NodeUser nodeUser);
	void infoAccount(NodeUser nodeUser);
}
