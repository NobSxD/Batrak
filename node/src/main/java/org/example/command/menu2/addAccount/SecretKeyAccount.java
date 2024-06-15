package org.example.command.menu2.addAccount;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.command.Command;
import org.example.crypto.CryptoUtils;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.ACCOUNT_NAME;
import static org.example.entity.enams.UserState.BASIC_STATE;

@Component
@RequiredArgsConstructor
public class SecretKeyAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final ProcessServiceCommand processServiceCommand;
	private final CryptoUtils cryptoUtils;
	private final Change change;

	@Override
	public String send(NodeUser nodeUser, String text) {
		String pKey = cryptoUtils.encryptMessage(text);
		try {
			Account changeAccount = nodeUser.getAccount();
			changeAccount.setSecretApiKey(pKey);
			nodeUser.setAccount(changeAccount);
			nodeUser.setState(BASIC_STATE);

			if (changeAccount.getNameAccount() == null || changeAccount.getPublicApiKey() == null || changeAccount.getSecretApiKey() == null){
				change.deleteFindId(changeAccount.getId());
				nodeUser.setState(ACCOUNT_NAME);
				nodeUserDAO.save(nodeUser);
				return "Имя акаунта или публичный ключ или секретный ключ не были введены, пожалуйста повторите попытку +\n" +
						"Введит имя аккаунта";
			}
			change.saveAccount(changeAccount, nodeUser);
			nodeUserDAO.save(nodeUser);

			processServiceCommand.menu2Selection("Вы успешго добавили аккаунт - " + changeAccount.getNameAccount(), nodeUser.getChatId());
			nodeUserDAO.save(nodeUser);
			return "";

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Не получилось сохранить секретный ключ,\n" +
					"Попробуйте еще раз, если не получиться обратитесь к администратору." ;
		}

	}

	@Override
	public UserState getType() {
		return UserState.SECRET_API;
	}
}
