package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.command.RoleProvider;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.entity.NodeUser;
import org.example.entity.enams.Role;
import org.example.entity.enams.state.UserState;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStop implements Command, RoleProvider {
	private final ProducerXChangeService producerXChangeService;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			producerXChangeService.stopTrade(nodeUser);
			nodeUser.setState(UserState.BASIC_STATE);
			return "Для избежание финансовых потерь дождитесь цикла завершения торговли";
		}catch (Exception e){
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время остановки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_STOP;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}


}
