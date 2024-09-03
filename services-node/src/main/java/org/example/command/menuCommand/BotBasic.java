package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BotBasic implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BASIC_STATE;
	}





}
