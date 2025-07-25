package org.example.command.menuBasic;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainMenu implements Command, RoleProvider {
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			producerTelegramService.menuMain( "Вы в главном меню",nodeUser.getChatId());
			nodeUser.setState(UserState.BASIC_STATE);
		}catch (Exception e){
			log.error("Ошибка: {}", e.getMessage());
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.BOT_MAIN_MENU;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}
}
