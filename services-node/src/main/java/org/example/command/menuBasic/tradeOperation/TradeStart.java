package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.TradeState;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerXChangeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStart implements Command {
	
	private final ProducerXChangeService producerXChangeService;
	
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			if (nodeUser.isTradeStartOrStop()) {
				nodeUser.setTradeStartOrStop(true);
				//TODO добавить 3 фазное завершение торговли
				nodeUser.setStateTrade(TradeState.BAY);
				nodeUser.setLastStartTread(LocalDateTime.now());
				producerXChangeService.startTread(nodeUser);
				return "";
			}
			return "Трейдинг уже запущен, отмените ордер или дождитесь завершение торговли.";
		}catch (Exception e){
			e.printStackTrace();
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_START;
	}


}
