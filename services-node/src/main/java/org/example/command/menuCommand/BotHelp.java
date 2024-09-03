package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProcessServiceCommand;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BotHelp implements Command {

	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		return processServiceCommand.helpAccount();
	}

	@Override
	public UserState getType() {
		return UserState.BOT_HELP;
	}



}
