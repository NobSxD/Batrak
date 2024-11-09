package org.example.service;

import org.example.dto.NodeUserDto;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static org.example.model.RabbitQueue.INFO_ACCOUNT;
import static org.example.model.RabbitQueue.TRADE_CANCEL_ORDER;
import static org.example.model.RabbitQueue.TRADE_MESSAGE;
import static org.example.model.RabbitQueue.TRADE_STATE;
import static org.example.model.RabbitQueue.TRADE_STOP;

public interface MainServiceTradeBot {
	
	@RabbitListener(queues =  TRADE_MESSAGE)
	void startTrade(NodeUserDto nodeUser);
	
	@RabbitListener(queues = TRADE_CANCEL_ORDER)
	void cancelOrder(NodeUserDto nodeUser);

	@RabbitListener(queues = INFO_ACCOUNT)
	void infoAccount(NodeUserDto nodeUser);

	@RabbitListener(queues = TRADE_STOP)
	void stopTrade(NodeUserDto nodeUser);

	@RabbitListener(queues = TRADE_STATE)
	void stateTrade(NodeUserDto nodeUserDto);
}
