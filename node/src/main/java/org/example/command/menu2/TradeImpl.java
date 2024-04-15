package org.example.command.menu2;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

@Component
public class TradeImpl implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		return "TradeImpl";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE;
	}

}
