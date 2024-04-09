package org.example.command.menu2.button;

import lombok.Data;
import org.example.command.processServiceCommand.ProcessServiceCommand;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.entity.enums.MenuEnums2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.ACCOUNT_NAME;
import static org.example.entity.enams.UserState.SECRET_API;


@Component
@Data
public class RegisterAccountImpl implements Menu2 {
	private final NodeUserDAO nodeUserDAO;
	private final PasswordEncoder passwordEncoder;
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser) {
		nodeUser.setState(ACCOUNT_NAME);
		nodeUserDAO.save(nodeUser);
		return "Ведите имя аккаунта";
	}

	public String accountName(NodeUser nodeUser, String text){
		nodeUser.setState(ACCOUNT_NAME);
		nodeUserDAO.save(nodeUser);
		return "Ведите имя аккаунта";
	}

	public String publicApi(NodeUser nodeUser, String text) {
		String encode = passwordEncoder.encode(text);
		Account account = nodeUser.getAccount();
		if (account == null){
			return "аккаунт не найден, попробуйте еще раз.";
		}
		nodeUser.setAccount(account);
		nodeUser.setState(SECRET_API);
		account.setPublicApiKey(encode);
		nodeUserDAO.save(nodeUser);
		return "ведите секретный ключ";

	}

	public String secretApi(NodeUser nodeUser, String text) {
		String encode = passwordEncoder.encode(text);
		Account account = nodeUser.getAccount();
		if (account == null){
			return "аккаунт не найден, попробуйте еще раз.";
		}
		account.setSecretApiKey(encode);
		nodeUser.setAccount(account);
		nodeUser.setState(UserState.ACCOUNT_USER);
		processServiceCommand.registerAccount(nodeUser);
		processServiceCommand.menuButtonAction("Вы успешго добавили аккаунт - " + account.getNameChange(), nodeUser.getChatId());
		nodeUserDAO.save(nodeUser);
		return "";
	}

	@Override
	public MenuEnums2 getType() {
		return MenuEnums2.RegisterAccount;
	}
}
