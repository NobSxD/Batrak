package org.example.command.state;

import lombok.Data;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@Data
public class HelpImpl implements Command {

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
