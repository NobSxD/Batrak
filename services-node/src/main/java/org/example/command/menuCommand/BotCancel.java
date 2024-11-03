package org.example.command.menuCommand;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.BASIC_STATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotCancel implements Command, RoleProvider {
	
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setState(BASIC_STATE);
		} catch (Exception e) {
			log.error("Имя пользователя: {}. Id пользователя {} , error: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
			return "во время отмены произошла ошибка, обратитесь к администратору системы.";
		}
		return "Команда отменена!";
		
	}
	
	@Override
	public UserState getType() {
		return UserState.BOT_CANCEL;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}

}
