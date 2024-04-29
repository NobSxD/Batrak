package org.example.command.menu2;

import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

@Component
public class TradeImpl implements Command {
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {

		}catch (Exception e){
			LoggerInFile.saveLogInFile(e.getMessage(), "TradeImpl");
			return "во время настройки трейдинга произошла ошибка, обратитесь к администратору системы.";
		}
		return "TradeImpl";
	}

	@Override
	public UserState getType() {
		return UserState.TRADE;
	}

}
