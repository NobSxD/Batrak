package org.example.processServiceCommand;

import org.example.command.change.Change;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;

public interface ProcessServiceChangeCommands {
	void saveAccount(Account account, NodeUser nodeUser);

	void changeAction(NodeUser nodeUser, String text);
	void registerAccount(NodeUser nodeUser);
	void ListAccaunts(String output, Long chatId); // получить список зарегестрированых аккаунтов.
	Change change(String nameChange);
	Account getChangeAccount(); // получить текущий аккаунт.
	Account getChangeAccount(String nameChange); // получить текущий аккаунт.
}
