package org.example.service;

import org.example.entity.NodeUser;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static org.example.model.RabbitQueue.INFO_ACCOUNT;
import static org.example.model.RabbitQueue.TRADE_CANCEL_ORDER;
import static org.example.model.RabbitQueue.TRADE_MESSAGE;
import static org.example.model.RabbitQueue.TRADE_STOP;

public interface MainServiceTradeBot {
	
	@RabbitListener(queues =  TRADE_MESSAGE)
	void startTrade(NodeUser nodeUser);
	
	@RabbitListener(queues = TRADE_CANCEL_ORDER)
	void cancelOrder(NodeUser nodeUser);

	@RabbitListener(queues = INFO_ACCOUNT)
	void infoAccount(NodeUser nodeUser);

	@RabbitListener(queues = TRADE_STOP)
	void stopTrade(NodeUser nodeUser);
}
