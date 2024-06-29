package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.dao.NodeUserDAO;
import org.example.service.ConsumerService;
import org.example.service.MainServiceTradeBot;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.example.model.RabbitQueue.TRADE_MESSAGE;


@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final Logger logger = Logger.getLogger(ConsumerServiceImpl.class);
    private final MainServiceTradeBot mainServiceTradeBot;
    private final NodeUserDAO nodeUserDAO;



 //   @Override
    @RabbitListener(queues =  TRADE_MESSAGE)
    public void consumeTexMessageUpdate(Map<String, Long> id  ) {

        var nodeUser = nodeUserDAO.findById(id.get("id")).orElse(null);
        logger.debug("TRADE_BOT: Text бот получил юзера");
        mainServiceTradeBot.startORStopTrade(nodeUser);
    }





}
