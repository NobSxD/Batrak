package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.service.QueueService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {
	private final RabbitAdmin rabbitAdmin;
	private final ConnectionFactory connectionFactory;

	@Override
	public void createQueueForUser(String userId) {
		String queueName = "userQueue-" + userId;
		Queue queue = new Queue(queueName, false);
		rabbitAdmin.declareQueue(queue);

		// Регистрация слушателя для очереди пользователя
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setQueueNames(queueName);
		container.setConnectionFactory(connectionFactory);
		container.setMessageListener(new MessageListenerAdapter(connectionFactory, "receiveMessage"));
		container.start();
	}
}
