package org.example.service.impl;

import org.example.castom.CustomMessage;
import org.example.castom.MessageWrapperDTO;
import org.example.dto.NodeUserDto;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.example.entity.Account;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;
import org.example.entity.enams.menu.MenuAdmin;
import org.example.entity.enams.menu.MenuBasic;
import org.example.entity.enams.menu.MenuOperation;
import org.example.entity.enams.menu.MenuSetting;
import org.example.entity.enams.menu.MenuStatistics;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE;
import static org.example.model.RabbitQueue.ENUM_CUSTOM_MESSAGE;
import static org.example.model.RabbitQueue.LIST_CUSTOM_MESSAGE;
import static org.example.model.RabbitQueue.REBUT_TELEGRAM;


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
    public void producerRebutTelegram(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        rabbitTemplate.convertAndSend(REBUT_TELEGRAM, customMessage);
    }

    @Override
    public void menuChange(List<NodeChange> nameChange, String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .object(nameChange)
                .build();
        rabbitTemplate.convertAndSend(LIST_CUSTOM_MESSAGE, messageWrapperDTO);
    }
    @Override
    public void menuAccounts(List<Account> accounts, String output, Long chatId) {
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
    public void menuPair(List<Pair> pairs, String output, Long chatId) {
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
    @Override
    public void menuBan(List<NodeUserDto> userName, String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .object(userName)
                .customMessage(customMessage)
                .build();
        rabbitTemplate.convertAndSend(LIST_CUSTOM_MESSAGE, messageWrapperDTO);
    }

    @Override
    public void menuMain(String output, Long chatId) {
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
    public void menuAdmin(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuAdmin.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);
    }

    @Override
    public void menuStatistics(String output, Long chatId) {
        CustomMessage customMessage = CustomMessage.builder()
                .chatId(chatId.toString())
                .text(output)
                .build();
        MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
                .customMessage(customMessage)
                .enumClass(MenuStatistics.class)
                .build();
        rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);
    }

}

