package org.example.service.impl;

import org.example.dto.NodeUserDto;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.INFO_ACCOUNT;
import static org.example.model.RabbitQueue.TRADE_CANCEL_ORDER;
import static org.example.model.RabbitQueue.TRADE_MESSAGE;
import static org.example.model.RabbitQueue.TRADE_STOP;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProducerXChangeServiceImpl implements ProducerXChangeService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void startTrade(NodeUser nodeUser) {
        NodeUserDto nodeUserDto = new NodeUserDto(nodeUser.getId(), nodeUser.getChatId(), nodeUser.getUsername());
        rabbitTemplate.convertAndSend(TRADE_MESSAGE, nodeUserDto);
    }

    @Override
    public void stopTrade(NodeUser nodeUser) {
        NodeUserDto nodeUserDto = new NodeUserDto(nodeUser.getId(), nodeUser.getChatId(), nodeUser.getUsername());
        rabbitTemplate.convertAndSend(TRADE_STOP, nodeUserDto);
    }

    @Override
    public void infoAccount(NodeUser nodeUser) {
        NodeUserDto nodeUserDto = new NodeUserDto(nodeUser.getId(), nodeUser.getChatId(), nodeUser.getUsername());
        rabbitTemplate.convertAndSend(INFO_ACCOUNT, nodeUserDto);
    }

    @Override
    public void cancelTread(NodeUser nodeUser) {
        NodeUserDto nodeUserDto = new NodeUserDto(nodeUser.getId(), nodeUser.getChatId(), nodeUser.getUsername());
        rabbitTemplate.convertAndSend(TRADE_CANCEL_ORDER, nodeUserDto);
    }
}
