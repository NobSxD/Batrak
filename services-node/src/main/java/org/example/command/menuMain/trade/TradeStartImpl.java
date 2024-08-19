package org.example.command.menuMain.trade;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.ProducerXChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TradeStartImpl implements Command {
	private static final Logger logger = LoggerFactory.getLogger(TradeStartImpl.class);
	private final ProducerXChangeService producerXChangeService;
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			nodeUser.setTradeStartOrStop(true);
			nodeUser.setLastStartTread(LocalDateTime.now());
			nodeUserDAO.save(nodeUser);
			producerXChangeService.startTread(nodeUser);
			return "Трейд запущен, начинаю искать точку входа";
		}catch (Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
			return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_START;
	}


}
