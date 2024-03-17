package org.example.command.state.impl;

import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.command.state.State;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Data
public class SecretApiImpl implements State {
	private final NodeUserDAO nodeUserDAO;
	private final PasswordEncoder passwordEncoder;
	private final ProcessServiceCommand processServiceCommand;


	@Override
	public String send(NodeUser nodeUser, String text) {
		String encode = passwordEncoder.encode(text);
		nodeUser.setSecretApi(encode);
		nodeUser.setState(UserState.ACCOUNT_USER);
		processServiceCommand.registerAccount(nodeUser);
		processServiceCommand.menuButtonAction("Вы успешго добавили аккаунт - " + nodeUser.getAccountName(), nodeUser.getChatId());
		nodeUserDAO.save(nodeUser);
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.SECRET_API;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
