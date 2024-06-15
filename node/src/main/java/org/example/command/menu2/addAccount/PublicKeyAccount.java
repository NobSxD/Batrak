package org.example.command.menu2.addAccount;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.command.Command;
import org.example.crypto.CryptoUtils;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.ACCOUNT_NAME;
import static org.example.entity.enams.UserState.SECRET_API;

@Component
@RequiredArgsConstructor
public class PublicKeyAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final CryptoUtils cryptoUtils;
	private final Change change;
	@Override
	public String send(NodeUser nodeUser, String text) {
		String pKey = cryptoUtils.encryptMessage(text);
		try {
			Account changeAccount = nodeUser.getAccount();
			nodeUser.setState(SECRET_API);
			changeAccount.setPublicApiKey(pKey);
			nodeUser.setAccount(changeAccount);

			if (changeAccount.getNameAccount() == null || changeAccount.getPublicApiKey() == null){
				change.deleteFindId(changeAccount.getId());
				nodeUser.setState(ACCOUNT_NAME);
				nodeUserDAO.save(nodeUser);
				return "Имя акаунта или публичный ключ не были введены, пожалуйста повторите попытку +\n" +
						"Введит имя аккаунта";
			}

			change.saveAccount(changeAccount, nodeUser);
			nodeUserDAO.save(nodeUser);
			return "ведите секретный ключ";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Не получилось сохранить публичный ключ,\n" +
					"Попробуйте еще раз, если не получиться обратитесь к администратору." ;
		}
	}

	@Override
	public UserState getType() {
		return UserState.PUBLIC_API;
	}
}
