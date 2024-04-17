package org.example.processServiceCommand;

import org.example.entity.NodeUser;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProcessServiceCommand {
	void menu2Selection(String output, Long chatId);
	void menu1ChoosingAnExchange(Long chatId, String message);
	void sendAnswer(String output, Long chatId);
	String helpAccount();
	boolean isNotAllowToSendContent(Long chatId, NodeUser appUser);
	NodeUser findOrSaveAppUser(Update update); // сохранение пользователя

}
