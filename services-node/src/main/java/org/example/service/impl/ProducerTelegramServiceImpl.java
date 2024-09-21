package org.example.service.impl;

import org.example.castom.CustomMessage;
import org.example.castom.MessageWrapperDTO;
import org.example.entity.Account;
import org.example.entity.collect.Pair;
import org.example.entity.enams.menu.MenuBasic;
import org.example.entity.enams.menu.MenuChange;
import org.example.entity.enams.menu.MenuOperation;
import org.example.entity.enams.menu.MenuSetting;
import org.example.entity.enams.menu.MenuStrategy;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE;
import static org.example.model.RabbitQueue.ENUM_CUSTOM_MESSAGE;
import static org.example.model.RabbitQueue.LIST_CUSTOM_MESSAGE;


@Service
@RequiredArgsConstructor
public class ProducerTelegramServiceImpl implements ProducerTelegramService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void producerAnswer(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, customMessage);
    }

    @Override
    public void changeMenu(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuChange.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);
    }

    @Override
    public void mainMenu(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuBasic.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);

    }

    @Override
    public void menuTrade(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuSetting.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);

    }

    @Override
    public void menuOperation(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuOperation.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);

    }


    @Override
    public void strategyMenu(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuStrategy.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);

    }

    @Override
    public void accountsMenu(List<Account> accounts, String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .object(accounts)
                .build();
        rabbitTemplate.convertAndSend(LIST_CUSTOM_MESSAGE, messageWrapperDTO);

    }

    @Override
    public void pairMenu(List<Pair> pairs, String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .object(pairs)
                .customMessage(customMessage)
                .build();
        rabbitTemplate.convertAndSend(LIST_CUSTOM_MESSAGE, messageWrapperDTO);

    }

}

