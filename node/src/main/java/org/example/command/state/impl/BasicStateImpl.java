package org.example.command.state.impl;

import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.command.state.State;
import org.example.service.ProducerService;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Data
public class BasicStateImpl implements State {
	private final ProducerService producerService;

	private final ProcessServiceCommand processServiceCommand;

	@Override
	public String send(NodeUser nodeUser, String text) {
		menuButtonAction(text, nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BASIC_STATE;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}

	private void menuButtonAction(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuEnumsButton(sendMessage);
	}
}
