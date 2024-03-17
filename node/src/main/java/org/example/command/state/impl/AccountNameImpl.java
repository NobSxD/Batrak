package org.example.command.state.impl;

import org.example.command.state.State;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AccountNameImpl implements State {
	private final NodeUserDAO nodeUserDAO;


	@Override
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setAccountName(text);
		nodeUser.setState(UserState.PUBLIC_API);
		nodeUserDAO.save(nodeUser);
		return "Вы вели имя аккаунта. Теперь введите публичный ключ";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_NAME;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
