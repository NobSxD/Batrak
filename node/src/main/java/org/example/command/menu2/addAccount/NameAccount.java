package org.example.command.menu2.addAccount;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.PUBLIC_API;

@Component
@RequiredArgsConstructor
public class NameAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final Change change;

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			Account changeAccount = change.getAccount(text, nodeUser);
			if (changeAccount == null) {
				changeAccount = change.newAccount(nodeUser);
				changeAccount.setNameAccount(text);
				nodeUser.setState(PUBLIC_API);
				nodeUser.setAccount(changeAccount);
				change.saveAccount(changeAccount, nodeUser);
				nodeUserDAO.save(nodeUser);
				return "Ведите публичный ключ";
			} else {
				return "Данное имя уже используеться, пожалайста укажите уникальное имя аккаунта";
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
			return "Не получилось сохранить имя аккаунта,\n" +
					"Попробуйте еще раз, если не получиться обратитесь к администратору." ;
		}
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_NAME;
	}
}
