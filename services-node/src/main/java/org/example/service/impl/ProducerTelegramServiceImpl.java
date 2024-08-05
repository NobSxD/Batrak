package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.castom.CustomMessage;
import org.example.castom.MessageWrapperDTO;
import org.example.entity.Account;
import org.example.entity.collect.Pair;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.MainMenu;
import org.example.entity.enams.SettingUpTrading;
import org.example.entity.enams.StrategyEnams;
import org.example.service.ProducerTelegramService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.model.RabbitQueue.*;


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
				.enumClass(ChangeType.class)
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
				.enumClass(MainMenu.class)
				.build();
		rabbitTemplate.convertAndSend(ENUM_CUSTOM_MESSAGE, messageWrapperDTO);

	}

	@Override
	public void settingUpTrading(String output, Long chatId) {
		CustomMessage customMessage = CustomMessage.builder()
				.chatId(chatId.toString())
				.text(output)
				.build();
		MessageWrapperDTO messageWrapperDTO = MessageWrapperDTO.builder()
				.customMessage(customMessage)
				.enumClass(SettingUpTrading.class)
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
				.enumClass(StrategyEnams.class)
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
		rabbitTemplate.convertAndSend(LIST_CUSTOM_MESSAGE,messageWrapperDTO);

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

