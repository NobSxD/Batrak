package org.example.service.impl;

import org.example.entity.account.Account;
import org.example.entity.enams.Menu1Enums;
import org.example.entity.enams.Menu2Enums;
import org.example.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.example.model.RabbitQueue.ANSWER_MESSAGE;


@Service
public class ProducerServiceImpl implements ProducerService {
	private final RabbitTemplate rabbitTemplate;

	public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void producerAnswer(SendMessage sendMessage) {
		rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
	}

	public void producerChangeEnumsButton(SendMessage sendMessages) {
		Menu1Enums[] changeEnums = Menu1Enums.values(); //список бирж для добавление

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

		for (Menu1Enums addChangeEnums : changeEnums) {
			List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
			keyboardButtonsRow.add(button(addChangeEnums.toString()));
			rowList.add(keyboardButtonsRow);
		}


		inlineKeyboardMarkup.setKeyboard(rowList);

		sendMessages.setReplyMarkup(inlineKeyboardMarkup);
		rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessages);

	}

	@Override
	public void producerMenuEnumsButton(SendMessage sendMessage) {
		Menu2Enums[] menuEnum2s = Menu2Enums.values(); //список бирж для добавление

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

		for (Menu2Enums addChangeEnums : menuEnum2s) {
			List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
			keyboardButtonsRow.add(button(addChangeEnums.toString()));
			rowList.add(keyboardButtonsRow);
		}


		inlineKeyboardMarkup.setKeyboard(rowList);

		sendMessage.setReplyMarkup(inlineKeyboardMarkup);
		rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);

	}

	@Override
	public void producerAccountButton(List<Account> accounts, SendMessage sendMessage) {


		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

		for (Account addChangeEnums : accounts  ) {
			List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
			keyboardButtonsRow.add(button(addChangeEnums.getNameChange()));
			rowList.add(keyboardButtonsRow);
		}


		inlineKeyboardMarkup.setKeyboard(rowList);

		sendMessage.setReplyMarkup(inlineKeyboardMarkup);
		rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);

	}

	private InlineKeyboardButton button(String text) {
		return InlineKeyboardButton.builder()
				.text(text)
				.callbackData(text)
				.build();
	}

}

