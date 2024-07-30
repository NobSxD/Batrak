package org.example.command.menuMain.trade;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeStartImpl implements Command {
	private static final Logger logger = LoggerFactory.getLogger(TradeStartImpl.class);
	private final ProcessServiceCommand processServiceCommand;
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setTradeStartOrStop(true);
			nodeUserDAO.save(nodeUser);
			processServiceCommand.startTread(nodeUser);
			return "Трейд запущен, начинаю искать точку входа";
		}catch (Exception e){
			logger.error(e.getMessage());
			return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_START;
	}


}
