package org.example.command.menuMain;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class CancelMenu2Impl implements Command {
	private final Logger logger = LoggerFactory.getLogger(CancelMenu2Impl.class);
	private final NodeUserDAO nodeUserDAO;
	@Override
	public String send(NodeUser nodeUser , String text) {
		try {
			nodeUser.setState(BASIC_STATE);
			nodeUserDAO.save(nodeUser);
		}catch (Exception e){
			logger.error(e.getMessage() +  " имя пользователя: " +  nodeUser.getUsername() + ". Id пользователя " + nodeUser.getId());
			return "во время отмены произошла ошибка, обратитесь к администратору системы.";
		}
		return "Команда отменена!";

	}

	@Override
	public UserState getType() {
		return UserState.BOT_CANCEL;
	}


}
