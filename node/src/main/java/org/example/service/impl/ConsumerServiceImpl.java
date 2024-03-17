package org.example.service.impl;

import org.example.service.ConsumerService;
import org.example.service.MainService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.TEXT_MESSAGE_UPDATE;


@Service
@Log4j2
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTexMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainService.defines(update);
    }





}
