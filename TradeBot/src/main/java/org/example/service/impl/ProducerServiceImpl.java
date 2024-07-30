package org.example.service.impl;

import org.example.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import static org.example.model.RabbitQueue.ANSWER_MESSAGE;


@Service
public class ProducerServiceImpl implements ProducerService {
	private final RabbitTemplate rabbitTemplate;

	public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void producerAnswer(String sendMessage) {
		rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
	}



}

