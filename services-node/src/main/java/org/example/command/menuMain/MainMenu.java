package org.example.command.menuMain;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenu implements Command {
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		producerTelegramService.mainMenu( "Вы в главном меню",nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_MAIN_MENU;
	}
}
