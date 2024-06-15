package org.example.command.menu2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static org.example.entity.enams.UserState.ACCOUNT_NAME;


@Component
@Getter
@Setter
@RequiredArgsConstructor
public class RegisterAccountImpl implements Command {
	private final NodeUserDAO nodeUserDAO;

	@Override
	@Transactional
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(ACCOUNT_NAME);
		nodeUserDAO.save(nodeUser);
		return "Ведите уникальное имя аккаунта";

	}

	@Override
	public UserState getType() {
		return UserState.REGISTER_ACCOUNT;
	}




}
