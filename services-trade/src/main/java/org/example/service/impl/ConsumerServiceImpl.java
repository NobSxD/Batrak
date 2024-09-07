package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.NodeUser;
import org.example.service.MainServiceTradeBot;
import org.example.service.ProcessServiceCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.TRADE_MESSAGE;


@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl {
    private final MainServiceTradeBot mainServiceTradeBot;
    private final ProcessServiceCommand processServiceCommand;


    @RabbitListener(queues =  TRADE_MESSAGE)
    public void consumeTexMessageUpdate(NodeUser nodeUser) {
        try {
            log.debug("TRADE_BOT: Text бот получил юзера: " + nodeUser.getUsername());
            mainServiceTradeBot.startORStopTrade(nodeUser);
            processServiceCommand.sendAnswer("Трейдинг запущен", nodeUser.getChatId());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }





}
