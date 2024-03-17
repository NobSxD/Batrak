package org.example.service.impl;

import org.example.entity.enums.ChangeEnums;
import org.example.entity.enums.MenuEnums2;
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
		ChangeEnums[] changeEnums = ChangeEnums.values(); //список бирж для добавление

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

		for (ChangeEnums addChangeEnums : changeEnums) {
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
		MenuEnums2[] menuEnum2s = MenuEnums2.values(); //список бирж для добавление

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

		for (MenuEnums2 addChangeEnums : menuEnum2s) {
			List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
			keyboardButtonsRow.add(button(addChangeEnums.toString()));
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

