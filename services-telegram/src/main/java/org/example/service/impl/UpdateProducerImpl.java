package org.example.service.impl;

import org.example.service.UpdateProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
public class UpdateProducerImpl implements UpdateProducer {
    private final Logger logger = LoggerFactory.getLogger(UpdateProducerImpl.class);
    private final RabbitTemplate rabbitTemplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        if (update.getMessage() != null) {
            logger.debug(update.getMessage().getText());
        } else if (update.getCallbackQuery().getData() != null) {
            logger.debug(update.getCallbackQuery().getData());
        }
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }

}
