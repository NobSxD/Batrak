package org.example.command.menu2;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.example.service.impl.LoggerInFile;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class CancelMenu2Impl implements Command {
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser , String text) {
		try {
			nodeUser.setState(BASIC_STATE);
			nodeUserDAO.save(nodeUser);
		}catch (Exception e){
			System.out.println(e.getMessage());
			LoggerInFile.saveLogInFile(e.getMessage(), "CancelMenu2Impl");
			return "во время отмены произошла ошибка, обратитесь к администратору системы.";
		}
		return "Команда отменена!";

	}

	@Override
	public UserState getType() {
		return UserState.BOT_CANCEL;
	}


}
