package org.example.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.example.command.CommandService;
import org.example.entity.RawData;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.MainService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;


@Service
@Log4j2
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private final ProcessServiceCommand processServiceCommand;
	private final CommandService commandService;
	private final Map<String, UserState> buttonRegistration;

	@Override
	@Transactional
	public void defines(Update update) {
		String text = "";
		long chatId = 0;

		saveRawData(update);
		var nodeUser = processServiceCommand.findOrSaveAppUser(update);
		//TODO на будующие, для активации пользователя
		if (nodeUser.getIsActive()){
			processServiceCommand.sendAnswer("В доступе отказанно, активируйте свой аккаунт", chatId);
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
		if (userState != null){
			nodeUser.setState(userState);
		}

		String send = commandService.send(nodeUser, text);
		processServiceCommand.sendAnswer(send, chatId);

	}



	@SneakyThrows
	private void saveRawData(Update update) {
		File file = new File("D:/project/new/treadeBot/node/src/main/resources/log/");
		file.mkdirs();

		File raw = new File(file, "rawData.txt");


		if (! raw.exists()) {
			raw.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(raw, true);
		RawData rawData = RawData.builder().event(update).build();
		String rawDataString = rawData.toString() + "\n";
		fileOutputStream.write(rawDataString.getBytes());
		fileOutputStream.close();


	}







}
