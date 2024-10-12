package org.example.command.menuBasic.addAccount;

import org.example.factory.account.NodeAccount;
import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.exampel.crypto.CryptoUtils;
import org.springframework.stereotype.Component;

import static org.example.entity.enams.state.UserState.ACCOUNT_ADD_NAME;
import static org.example.entity.enams.state.UserState.BASIC_STATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddSecretKey implements Command {
	private final NodeAccount nodeAccountDAO;
	private final ProducerTelegramService producerTelegramService;


	@Override
	public String send(NodeUser nodeUser, String text) {
		CryptoUtils cryptoUtils = new CryptoUtils();
		String pKey = cryptoUtils.encryptMessage(text);
		try {
			Account changeAccount = nodeUser.getAccount();
			changeAccount.setSecretApiKey(pKey);
			changeAccount.setChangeType(nodeUser.getChangeType());
			nodeUser.setAccount(changeAccount);
			nodeUser.setState(BASIC_STATE);

			if (changeAccount.getNameAccount() == null || changeAccount.getPublicApiKey() == null || changeAccount.getSecretApiKey() == null){
				nodeAccountDAO.deleteFindId(changeAccount.getId());
				nodeUser.setState(ACCOUNT_ADD_NAME);
				return "Имя акаунта или публичный ключ или секретный ключ не были введены, пожалуйста повторите попытку +\n" +
						"Введит имя аккаунта";
			}

			nodeAccountDAO.saveAccount(changeAccount, nodeUser);
			producerTelegramService.mainMenu("Вы успешго добавили аккаунт - " + changeAccount.getNameAccount(), nodeUser.getChatId());
			return "";

		} catch (Exception e) {
			log.error("Ошибка: {}", e.getMessage());
			return "Не получилось сохранить секретный ключ,\n" +
					"Попробуйте еще раз, если не получиться обратитесь к администратору." ;
		}
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_ADD_SECRET_API;
	}
}
