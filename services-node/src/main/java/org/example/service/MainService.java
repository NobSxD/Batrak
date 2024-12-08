package org.example.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import static org.example.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

public interface MainService {
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    void processUpdate(Update update);

}
