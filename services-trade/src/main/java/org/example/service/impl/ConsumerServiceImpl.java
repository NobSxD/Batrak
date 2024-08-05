package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.NodeUser;
import org.example.service.MainServiceTradeBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.TRADE_MESSAGE;


@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    private final MainServiceTradeBot mainServiceTradeBot;


    @RabbitListener(queues =  TRADE_MESSAGE)
    public void consumeTexMessageUpdate(NodeUser nodeUser) {
        try {
            logger.debug("TRADE_BOT: Text бот получил юзера: " + nodeUser.getUsername());
            mainServiceTradeBot.startORStopTrade(nodeUser);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }





}
