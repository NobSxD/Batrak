package org.example.command.state.impl;

import org.example.command.state.State;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.SECRET_API;


@Component
@Data
public class PublicApiImpl implements State {
	private final NodeUserDAO nodeUserDAO;
	private final PasswordEncoder passwordEncoder;

	@Override
	public String send(NodeUser nodeUser, String text) {
		String encode = passwordEncoder.encode(text);
		nodeUser.setState(SECRET_API);
		nodeUser.setPublicApi(encode);
		nodeUserDAO.save(nodeUser);
		return "ведите секретный ключ";

	}

	@Override
	public UserState getType() {
		return UserState.PUBLIC_API;
	}

	@Override
	public Account getAccount(Account account) {
		return null;
	}
}
