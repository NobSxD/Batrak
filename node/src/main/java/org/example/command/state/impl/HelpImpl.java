package org.example.command.state.impl;

import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.command.state.State;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class HelpImpl implements State {

	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		return processServiceCommand.helpAccount();
	}

	@Override
	public UserState getType() {
		return UserState.HELP;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
