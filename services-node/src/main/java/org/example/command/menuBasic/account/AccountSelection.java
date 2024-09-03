package org.example.command.menuBasic.account;

import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountSelection implements Command {
	private final ProducerTelegramService producerTelegramService;
	private final NodeAccount nodeAccount;

	@Override
	public String send(NodeUser nodeUser, String nameAccount) {
		try {
			Account changeAccount = nodeAccount.getAccount(nameAccount, nodeUser);
			if (changeAccount == null){
				return "Аккаунт не найден, обновите страницу";
			}
			nodeAccount.saveAccount(changeAccount, nodeUser);
			nodeUser.setAccount(changeAccount);
			producerTelegramService.mainMenu("Вы выбрали аккаунт " + changeAccount.getNameAccount(), nodeUser.getChatId());
		} catch (Exception e){
			log.error("Ошибка: {}", e.getMessage());
			return "Не получилось выбрать аккаунт";
		}
		return "";
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_SELECT;
	}
}
