package org.example.command.state.impl;

import org.example.command.state.State;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class InfoAccountImpl implements State {


	@Override
	public String send(NodeUser nodeUser, String text) {
		return "";
	}



	@Override
	public UserState getType() {
		return UserState.INFO_ACCOUNT;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
