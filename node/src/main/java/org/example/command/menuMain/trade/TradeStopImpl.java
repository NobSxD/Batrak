package org.example.command.menuMain.trade;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeStopImpl implements Command {
	private static final Logger logger = Logger.getLogger(TradeStopImpl.class);
	private final NodeUserDAO nodeUserDAO;

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setTradeStartOrStop(false);
			nodeUserDAO.save(nodeUser);
			return "Для избежание финансовых потерь дождитесь цыкл завершения торговли";
		}catch (Exception e){
			logger.error(e.getMessage());
			return "во время остановки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_STOP;
	}


}
