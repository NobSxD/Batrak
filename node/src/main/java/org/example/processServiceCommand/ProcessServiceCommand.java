package org.example.processServiceCommand;

import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface ProcessServiceCommand {
	void menu3TradeSettings(String output, Long chatId);
	void menu2Selection(String output, Long chatId);
	void menu1ChoosingAnExchange(String output, Long chatId);
	void listStrategy(String output, Long chatId);
	void listAccount(List<Account> accounts, String output, Long chatId);
	void sendAnswer(String output, Long chatId);
	String helpAccount();
	boolean isNotAllowToSendContent(Long chatId, NodeUser appUser);
	NodeUser findOrSaveAppUser(Update update); // сохранение пользователя

}
