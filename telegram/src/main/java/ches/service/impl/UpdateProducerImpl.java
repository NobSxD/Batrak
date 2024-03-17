package ches.service.impl;

import ches.service.UpdateProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
@Log4j2
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        if (update.getMessage() != null) {
            log.debug(update.getMessage().getText());
        } else if (update.getCallbackQuery().getData() != null) {
            log.debug(update.getCallbackQuery().getData());
        }
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }

}
