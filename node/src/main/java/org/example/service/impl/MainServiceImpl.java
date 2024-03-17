package org.example.service.impl;

import org.example.command.menu2.Menu2Service;
import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.command.state.StateService;
import org.example.entity.RawData;
import org.example.entity.enums.MenuEnums2;
import org.example.service.MainService;
import org.example.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;

import static org.example.entity.enams.UserState.*;


@Service
@Log4j2
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private final ProducerService producerService;
	private final NodeUserDAO nodeUserDAO;
	private final Menu2Service menu2Service;
	private final StateService stateService;
	private final ProcessServiceCommand processServiceCommand;


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
		var userMain = findOrSaveAppUser(update);
		var userState = userMain.getState();
		long chatId = update.getCallbackQuery().getMessage().getChatId();
		if (userState.equals(CHANGE)) {
			processServiceCommand.changeAction(userMain, text);
			processServiceCommand.menuButtonAction(text, chatId);
		} else if (userState.equals(ACCOUNT_USER)) {
			menuAction(userMain, text);
		}

	}

	public void menuAction(NodeUser nodeUser, String text) {
		var textMenu = MenuEnums2.fromValue(text);
		assert textMenu != null;
		String send = menu2Service.send(textMenu, nodeUser);
		processServiceCommand.sendAnswer(send, nodeUser.getChatId());
	}

	@Transactional
	public void processTextMessage(Update update) {
		saveRawData(update);
		var nodeUser = findOrSaveAppUser(update);
		long chatId = update.getMessage().getChatId();
		nodeUser.setChatId(chatId);
		processServiceCommand.processServiceCommand(nodeUser, update);

		var output = stateService.send(nodeUser, update.getMessage().getText());

		processServiceCommand.sendAnswer(output, chatId);

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


	private boolean isNotAllowToSendContent(Long chatId, NodeUser appUser) {
		var userState = appUser.getState();
		if (! appUser.getIsActive()) {
			var error = "Зарегистрируйтесь или активируйте " + "свою учетную запись для загрузки контента.";
			processServiceCommand.sendAnswer(error, chatId);
			return true;
		} else if (! BASIC_STATE.equals(userState)) {
			var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
			processServiceCommand.sendAnswer(error, chatId);
			return true;
		}
		return false;
	}

	private NodeUser findOrSaveAppUser(Update update) {

		var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
		var appUserOpt = nodeUserDAO.findByTelegramUserId(telegramUser.getId());
		if (appUserOpt.isEmpty()) {
			NodeUser transientAppUser = NodeUser.builder().telegramUserId(telegramUser.getId()).username(telegramUser.getUserName()).firstName(telegramUser.getFirstName()).lastName(telegramUser.getLastName()).isActive(false).state(BASIC_STATE).build();
			return nodeUserDAO.save(transientAppUser);
		}
		return appUserOpt.get();
	}
}
