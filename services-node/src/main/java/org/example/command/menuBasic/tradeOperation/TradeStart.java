package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.entity.NodeUser;
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
			nodeUser.setTradeStartOrStop(true);
			nodeUser.setLastStartTread(LocalDateTime.now());
			producerXChangeService.startTread(nodeUser);
			return "Трейд запущен, начинаю искать точку входа";
		}catch (Exception e){
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_START;
	}


}
