package org.example.command.menu2;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeStopImpl implements Command {
	private final Change change;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			change.tradeStop(nodeUser);
		}catch (Exception e){
			LoggerInFile.saveLogInFile(e.getMessage(), "TradeStopImpl");
			return "во время остановки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
		return "Трейдинг остановлен";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_STOP;
	}


}
