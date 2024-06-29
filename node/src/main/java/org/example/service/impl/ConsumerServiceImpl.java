package org.example.service.impl;

import org.apache.log4j.Logger;
import org.example.service.ConsumerService;
import org.example.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.TEXT_MESSAGE_UPDATE;


@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final Logger logger = Logger.getLogger(ConsumerServiceImpl.class);
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTexMessageUpdate(Update update) {
        logger.debug("NODE: Text message is received");
        mainService.defines(update);
    }





}
