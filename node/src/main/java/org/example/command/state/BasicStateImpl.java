package org.example.command.state;

import lombok.Data;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Data
public class BasicStateImpl implements Command {
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




	private void menuButtonAction(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuEnumsButton(sendMessage);
	}
}
