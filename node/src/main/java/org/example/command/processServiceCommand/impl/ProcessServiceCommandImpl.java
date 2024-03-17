package org.example.command.processServiceCommand.impl;

import org.example.command.change.Change;
import org.example.command.change.ChangeServiceNode;
import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.entity.enums.ChangeEnums;
import org.example.entity.enums.ServiceCommand;
import org.example.service.ProducerService;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	private final ChangeServiceNode changeServiceNode;
	private Change change;

	@Override
	public void processServiceCommand(NodeUser nodeUser, Update update) {
		var text = update.getMessage().getText();
		var serviceCommand = ServiceCommand.fromValue(text);

		if (serviceCommand == null) {
			serviceCommand = ServiceCommand.UNKNOWN;
		}

		if (serviceCommand.equals(ServiceCommand.START)) {
			nodeUser.setState(CHANGE);
			nodeUserDAO.save(nodeUser);
			menuButtonChange(update.getMessage().getChatId(), "выбирите биржу");
		} else if (serviceCommand.equals(ServiceCommand.HELP)) {
			nodeUser.setState(HELP);
			nodeUserDAO.save(nodeUser);
		} else if (serviceCommand.equals(ServiceCommand.CANCEL)) {
			nodeUser.setState(CANCEL);
			nodeUserDAO.save(nodeUser);
		}


	}

	@Override
	public void changeAction(NodeUser nodeUser, String text) {
		change = getChange(text);
		nodeUser.setState(ACCOUNT_USER);
		nodeUserDAO.save(nodeUser);

	}

	@Override
	public Change getChange(String nameChange) {
		ChangeEnums changeEnums = ChangeEnums.fromValue(nameChange);
		change = changeServiceNode.change(changeEnums);
		return change;
	}

	@Override
	public void menuButtonChange(Long chatId, String message) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(message);
		producerService.producerChangeEnumsButton(sendMessage);
	}
	@Override
	public void menuButtonAction(String output, Long chatId) {
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
		if (change == null){
			menuButtonChange(nodeUser.getChatId(), "Вы не выбрали биржу, для продолжение выбирите что бы продолжить");
		}
		Account account = change.getAccount();
		account.setNameChange(nodeUser.getAccountName());
		account.setPublicApiKey(nodeUser.getPublicApi());
		account.setSecretApiKey(nodeUser.getSecretApi());
		nodeUser.setState(ACCOUNT_USER);
		nodeUserDAO.save(nodeUser);
		change.saveAccount(account, nodeUser);
	}

	@Override
	public void sendAnswer(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerAnswer(sendMessage);
	}




}
