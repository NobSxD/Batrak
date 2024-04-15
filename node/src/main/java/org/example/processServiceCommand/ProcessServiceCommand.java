package org.example.processServiceCommand;

import org.example.command.change.Change;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProcessServiceCommand {

	String helpAccount();
	void menu2Selection(String output, Long chatId);
	void menu1ChoosingAnExchange(Long chatId, String message);
	void changeAction(NodeUser nodeUser, String text);
	void registerAccount(NodeUser nodeUser);
	void sendAnswer(String output, Long chatId);
	Change change(String nameChange);
	Account getChangeAccount();
	boolean isNotAllowToSendContent(Long chatId, NodeUser appUser);
	NodeUser findOrSaveAppUser(Update update);
}
