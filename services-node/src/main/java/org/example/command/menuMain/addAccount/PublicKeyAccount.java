package org.example.command.menuMain.addAccount;

import lombok.RequiredArgsConstructor;
import org.exampel.crypto.CryptoUtils;
import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.UserState.ACCOUNT_ADD_NAME;
import static org.example.entity.enams.UserState.ACCOUNT_ADD_SECRET_API;

@Component
@RequiredArgsConstructor
public class PublicKeyAccount implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final NodeAccount nodeAccount;

	@Override
	public String send(NodeUser nodeUser, String text) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		String pKey = cryptoUtils.encryptMessage(text);
		try {
			Account changeAccount = nodeUser.getAccount();
			nodeUser.setState(ACCOUNT_ADD_SECRET_API);
			changeAccount.setPublicApiKey(pKey);
			nodeUser.setAccount(changeAccount);

			if (changeAccount.getNameAccount() == null || changeAccount.getPublicApiKey() == null){
				nodeAccount.deleteFindId(changeAccount.getId());
				nodeUser.setState(ACCOUNT_ADD_NAME);
				nodeUserDAO.save(nodeUser);
				return "Имя акаунта или публичный ключ не были введены, пожалуйста повторите попытку +\n" +
						"Введит имя аккаунта";
			}

			nodeAccount.saveAccount(changeAccount, nodeUser);
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
		return UserState.ACCOUNT_ADD_PUBLIC_API;
	}
}
