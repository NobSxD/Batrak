package org.example.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.dao.NodeChangeDAO;
import org.example.entity.NodeUser;
import org.example.service.MainServiceTradeBot;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.TRADE_MESSAGE;


@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl {
    private final Logger logger = Logger.getLogger(ConsumerServiceImpl.class);
    private final MainServiceTradeBot mainServiceTradeBot;
    private final ObjectMapper objectMapper;
    private final NodeChangeDAO nodeChangeDAO;

    @RabbitListener(queues =  TRADE_MESSAGE)
    public void consumeTexMessageUpdate(String message) {
        try {
            NodeUser nodeUser = objectMapper.readValue(message, NodeUser.class);
            nodeUser.setNodeChange(nodeChangeDAO.findByChangeType(nodeUser.getChangeType()).orElse(nodeUser.getNodeChange()));
            logger.debug("TRADE_BOT: Text бот получил юзера: " + nodeUser.getUsername());
            mainServiceTradeBot.startORStopTrade(nodeUser);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }





}
