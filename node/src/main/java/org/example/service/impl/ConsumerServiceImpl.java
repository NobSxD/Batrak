package org.example.service.impl;


import org.example.service.ConsumerService;
import org.example.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.TEXT_MESSAGE_UPDATE;


@Service
public class ConsumerServiceImpl implements ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);

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
