package com.example.node.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.example.node.service.ConsumerService;
import com.example.node.service.MainService;

import static org.example.model.RabbitQueue.*;


@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTexMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = TEXT_BUTTON)
    public void consumeTexButtonUpdate(Update update){
        log.debug("NODE: Text message is button");
        mainService.processTextButton(update);
    }




}
