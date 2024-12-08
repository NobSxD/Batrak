package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuOperation implements Command, RoleProvider {
	private final ProducerTelegramService producerTelegramService;
	
	@Override
	public String send(NodeUser nodeUser, String text) {
		producerTelegramService.menuOperation(text, nodeUser.getChatId());
		nodeUser.setState(UserState.BASIC_STATE);
		return "";
	}
	
	@Override
	public UserState getType() {
		return UserState.TRADE_OPERATION;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}
}
