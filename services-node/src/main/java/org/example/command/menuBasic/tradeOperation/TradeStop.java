package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStop implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setTradeStartOrStop(false);
			return "Для избежание финансовых потерь дождитесь цыкл завершения торговли";
		}catch (Exception e){
			log.error("Пользовтель: {}. id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(),  e.getMessage());
			return "во время остановки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_STOP;
	}


}
