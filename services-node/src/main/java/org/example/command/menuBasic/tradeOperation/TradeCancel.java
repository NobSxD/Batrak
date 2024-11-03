package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeCancel implements Command, RoleProvider {
	private final ProducerXChangeService producerXChangeService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			producerXChangeService.cancelTread(nodeUser);
			return "";
		}catch (Exception e){
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время отмены ордера произошла ошибка, обратитесь к администратору системы.";
		}
	}
	
	@Override
	public UserState getType() {
		return UserState.TRADE_CANCEL;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}
}
