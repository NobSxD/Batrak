package org.example.command.state.impl;

import org.example.command.state.State;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;

@Component
@Data
public class CancelImpl implements State {
	private final NodeUserDAO nodeUserDAO;


	@Override
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(BASIC_STATE);
		nodeUserDAO.save(nodeUser);
		return "Команда отменена!";
	}

	@Override
	public UserState getType() {
		return UserState.CANCEL;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
