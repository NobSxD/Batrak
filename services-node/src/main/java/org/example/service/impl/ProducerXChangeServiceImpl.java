package org.example.service.impl;

import org.example.entity.NodeUser;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.TRADE_CANCEL_ORDER;
import static org.example.model.RabbitQueue.TRADE_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProducerXChangeServiceImpl implements ProducerXChangeService {
	private final RabbitTemplate rabbitTemplate;

	@Override
	public void startTread(NodeUser nodeUser) {
		rabbitTemplate.convertAndSend(TRADE_MESSAGE, nodeUser);
	}
	
	@Override
	public void cancelTread(NodeUser nodeUser) {
		rabbitTemplate.convertAndSend(TRADE_CANCEL_ORDER, nodeUser);
	}
}
