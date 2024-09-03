package org.example.command.menuBasic.addAccount;

import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.ACCOUNT_ADD_PUBLIC_API;

@Component
@RequiredArgsConstructor
public class AddName implements Command {
	private final NodeAccount nodeAccount;

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			Account changeAccount = nodeAccount.getAccount(text, nodeUser);
			if (changeAccount == null) {
				changeAccount = nodeAccount.newAccount(nodeUser);
				changeAccount.setNameAccount(text);
				nodeUser.setState(ACCOUNT_ADD_PUBLIC_API);
				nodeUser.setAccount(changeAccount);
				nodeAccount.saveAccount(changeAccount, nodeUser);
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
		return UserState.ACCOUNT_ADD_NAME;
	}
}
