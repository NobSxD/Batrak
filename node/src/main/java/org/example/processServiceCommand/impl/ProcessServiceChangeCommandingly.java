package org.example.processServiceCommand.impl;

import lombok.Data;
import org.example.command.change.Change;
import org.example.command.change.ChangeServiceNode;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enums.Menu1Enums;
import org.example.processServiceCommand.ProcessServiceChangeCommands;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.example.entity.enams.UserState.ACCOUNT_USER;

@Component
@Data
public class ProcessServiceChangeCommandingly implements ProcessServiceChangeCommands {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	private final ChangeServiceNode changeServiceNode;
	private Change change;

	@Override
	public void changeAction(NodeUser nodeUser, String text) {
		change = change(text);
		nodeUser.setState(ACCOUNT_USER);
		nodeUserDAO.save(nodeUser);

	}
	@Override
	public Account getChangeAccount(){
		return change.getAccount();
	}

	@Override
	public Account getChangeAccount(String nameChange) {
		return change.getAccount(nameChange);
	}

	@Override
	public Change change(String nameChange) {
		Menu1Enums changeEnums = Menu1Enums.fromValue(nameChange);
		change = changeServiceNode.change(changeEnums);
		return change;
	}
	@Override
	public void registerAccount(NodeUser nodeUser){
		Account account = nodeUser.getAccount();
		nodeUser.setState(ACCOUNT_USER);
		change.saveAccount(account, nodeUser);
		nodeUserDAO.save(nodeUser);
	}

	@Override
	public void ListAccaunts(String output, Long chatId){
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerAccountButton(change.getAccounts(), sendMessage);
	}

	public void saveAccount(Account account, NodeUser nodeUser){
		change.saveAccount(account, nodeUser);
	}
}
