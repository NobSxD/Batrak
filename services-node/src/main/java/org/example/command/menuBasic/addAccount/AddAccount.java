package org.example.command.menuBasic.addAccount;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.ACCOUNT_ADD_NAME;


@Component
@Getter
@Setter
@RequiredArgsConstructor
public class AddAccount implements Command, RoleProvider {

	@Override
	@Transactional
	public String send(NodeUser nodeUser, String text) {
		nodeUser.setState(ACCOUNT_ADD_NAME);
		return "Ведите уникальное имя аккаунта";
	}
	@Override
	public UserState getType() {
		return UserState.ACCOUNT_ADD_REGISTER;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}


}
