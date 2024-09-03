package org.example.service.impl;

import org.example.command.CommandService;
import org.example.entity.enams.state.UserState;
import org.example.service.MainService;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerTelegramService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private final ProcessServiceCommand processServiceCommand;
	private final ProducerTelegramService producerTelegramService;
	private final CommandService commandService;
	private final Map<String, UserState> buttonRegistration;

	@Override
	@Transactional
	public void defines(Update update) {
		try {
			
			String text = "";
			long chatId = 0;
			
			var nodeUser = processServiceCommand.findOrSaveAppUser(update);
			//TODO на будующие, для активации пользователя
			if (nodeUser.getIsActive()) {
				producerTelegramService.producerAnswer("В доступе отказанно, активируйте свой аккаунт", chatId);
				return;
			}
			
			if (update.getMessage() != null) {         //при вводе пользователем сообщения
				text = update.getMessage().getText();
				chatId = update.getMessage().getChatId();
			}
			if (update.getCallbackQuery() != null) {  // при нажатие на кнопку пользователем
				text = update.getCallbackQuery().getData();
				chatId = update.getCallbackQuery().getMessage().getChatId();
			}
			
			UserState userState = buttonRegistration.get(text);
			if (userState != null) {
				nodeUser.setState(userState);
			}
			nodeUser.setChatId(chatId);
			String send = commandService.send(nodeUser, text);
			if (! send.equals("")) {
				producerTelegramService.producerAnswer(send, chatId);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}



}
