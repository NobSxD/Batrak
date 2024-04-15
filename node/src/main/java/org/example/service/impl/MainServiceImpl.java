package org.example.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.example.command.CommandService;
import org.example.dao.NodeUserDAO;
import org.example.entity.RawData;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.MainService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.example.entity.enams.UserState.*;


@Service
@Log4j2
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private final NodeUserDAO nodeUserDAO;
	private final ProcessServiceCommand processServiceCommand;
	private final CommandService commandService;

	@Override
	public void defines(Update update) {

		if (update.getMessage() != null) {
			processTextMessage(update);
		}
		if (update.getCallbackQuery() != null) {
			processTextButton(update);
		}

	}


	@Transactional
	public void processTextButton(Update update) {
		String text = update.getCallbackQuery().getData();
		saveRawData(update);
		var nodeUser = processServiceCommand.findOrSaveAppUser(update);
		long chatId = update.getCallbackQuery().getMessage().getChatId();
		UserState userState = clickButton(text);
		if (userState != null){
			nodeUser.setState(userState);
		}
		String send = commandService.send(nodeUser, text);
		processServiceCommand.sendAnswer(send, chatId);

	}


	@Transactional
	public void processTextMessage(Update update) {
		String text = update.getMessage().getText();
		saveRawData(update);
		var nodeUser = processServiceCommand.findOrSaveAppUser(update);
		long chatId = update.getMessage().getChatId();
		UserState userState = clickButton(text);
		if (userState != null){
			nodeUser.setState(userState);
		}
		String send = commandService.send(nodeUser, text);
		processServiceCommand.sendAnswer(send, chatId);

	}

	private UserState clickButton(String textButton){
		Map<String, UserState> userStateMap = new HashMap<>();
		userStateMap.put("/cancel", CANCEL);
		userStateMap.put("отмена", CANCEL);
		userStateMap.put("/start", START);
		userStateMap.put("/help", HELP);
		userStateMap.put("выбор аккаунта", ACCOUNT_SELECTION);
		userStateMap.put("регистрация", REGISTER_ACCOUNT);
		userStateMap.put("Настрайки трейдинга", MANAGER_TRADE);
		userStateMap.put("запуск трейдинга", TRADE_START);
		userStateMap.put("остановить трейдинг", TRADE_STOP);
		return userStateMap.get(textButton);
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
