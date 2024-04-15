package org.example.processServiceCommand.impl;

import lombok.Data;
import org.example.command.change.Change;
import org.example.command.change.ChangeServiceNode;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enums.Menu1Enums;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.entity.enams.UserState.ACCOUNT_USER;
import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	private final ChangeServiceNode changeServiceNode;
	private Change change;

	@Override
	public void changeAction(NodeUser nodeUser, String text) {
		change = change(text);
		nodeUser.setAccount(change.getAccount());
		nodeUser.setState(ACCOUNT_USER);
		change.saveAccount(change.getAccount(), nodeUser);
		nodeUserDAO.save(nodeUser);

	}


	@Override
	public Account getChangeAccount(){
		return change.getAccount();
	}


	@Override
	public Change change(String nameChange) {
		Menu1Enums changeEnums = Menu1Enums.fromValue(nameChange);
		change = changeServiceNode.change(changeEnums);
		return change;
	}

	@Override
	public void menu1ChoosingAnExchange(Long chatId, String message) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(message);
		producerService.producerChangeEnumsButton(sendMessage);
	}
	@Override
	public void menu2Selection(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuEnumsButton(sendMessage);
	}



	@Override
	public String helpAccount() {
		return """
				Выбирите действие:
				/infoBalance - информация о балансе;
				/pair - выбор пары;
				/cancel - отмена выполнения текущей команды;
				""";

	}

	@Override
	public void registerAccount(NodeUser nodeUser){
		Account account = nodeUser.getAccount();
		nodeUser.setState(ACCOUNT_USER);
		change.saveAccount(account, nodeUser);
		nodeUserDAO.save(nodeUser);
	}

	@Override
	public void sendAnswer(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerAnswer(sendMessage);
	}

	@Override
	public boolean isNotAllowToSendContent(Long chatId, NodeUser appUser) {
		var userState = appUser.getState();
		if (! appUser.getIsActive()) {
			var error = "Зарегистрируйтесь или активируйте " + "свою учетную запись для загрузки контента.";
			sendAnswer(error, chatId);
			return true;
		} else if (! BASIC_STATE.equals(userState)) {
			var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
			sendAnswer(error, chatId);
			return true;
		}
		return false;
	}
	@Override
	public NodeUser findOrSaveAppUser(Update update) {

		var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
		var appUserOpt = nodeUserDAO.findByTelegramUserId(telegramUser.getId());
		if (appUserOpt.isEmpty()) {
			NodeUser transientAppUser = NodeUser.builder().telegramUserId(telegramUser.getId()).username(telegramUser.getUserName()).firstName(telegramUser.getFirstName()).lastName(telegramUser.getLastName()).isActive(false).state(BASIC_STATE).build();
			return nodeUserDAO.save(transientAppUser);
		}
		return appUserOpt.get();
	}




}
