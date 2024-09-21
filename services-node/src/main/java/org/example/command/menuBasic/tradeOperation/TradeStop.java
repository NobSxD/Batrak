package org.example.command.menuBasic.tradeOperation;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.TradeState;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeStop implements Command {

	/**
	 * Изменяет состояние трейдинга в сервисе service-trade.
	 * В сервисе периодически проверяется состояние трейдинга и производится отмена,
	 * если это необходимо.
	 */
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			//TODO сделать остановку трейденга на подобие отмены
			nodeUser.setStateTrade(TradeState.TRADE_STOP);
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


}
