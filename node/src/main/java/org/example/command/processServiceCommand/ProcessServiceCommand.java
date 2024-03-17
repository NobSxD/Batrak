package org.example.command.processServiceCommand;

import org.example.command.change.Change;
import org.example.entity.NodeUser;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProcessServiceCommand {
	void processServiceCommand(NodeUser nodeUser, Update update);
	String helpAccount();
	void menuButtonAction(String output, Long chatId);
	void menuButtonChange(Long chatId, String message);
	void changeAction(NodeUser nodeUser, String text);
	void registerAccount(NodeUser nodeUser);
	void sendAnswer(String output, Long chatId);
	Change getChange(String nameChange);
}
