package org.example.command.menu2;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

@Component
public class ChoiceAccountImpl implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		return "ChoiceAccountImpl";
	}

	@Override
	public UserState getType() {
		return UserState.CHOICE;
	}


}
