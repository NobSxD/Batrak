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
public class TradeStartImpl implements Command {
	private final Change change;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			change.tradeStart(nodeUser);

		}catch (Exception e){
			e.printStackTrace();
			LoggerInFile.saveLogInFile(e.getMessage(), "TradeStartImpl");
			return "во время старта трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
		return "Трейд запущен, начинаю искать точку входа";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE_START;
	}


}
