package org.example.command.menuMain;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenu implements Command {
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		processServiceCommand.menu2Selection( "Вы в главном меню",nodeUser.getChatId());
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_MAIN_MENU;
	}
}
