package org.example.command.menuBasic.account;

import org.example.button.ButtonBasic;
import org.example.change.account.NodeAccount;
import org.example.command.Command;
import org.example.entity.Account;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;
import org.example.service.ProducerTelegramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountList implements Command {
	
	private final ProducerTelegramService producerTelegramService;
	private final NodeAccount nodeAccount;
	
	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			List<Account> accounts = nodeAccount.getAccounts(nodeUser);
			if (accounts.isEmpty()) {
				producerTelegramService.mainMenu("У вас нет аккаунта для выбора, сначало добавте аккаунт:", nodeUser.getChatId());
				return "";
			}
			producerTelegramService.accountsMenu(accounts, "Выберите аккаунт", nodeUser.getChatId());
			if (text.equals(ButtonBasic.deleteAccount)) {
				nodeUser.setState(UserState.ACCOUNT_DELETE);
			} else {
				nodeUser.setState(UserState.ACCOUNT_SELECT);
			}
			return "";
		} catch (Exception e) {
			log.error("Имя пользователя: {}. Пользователь id: {}. Ошибка: {}", nodeUser.getUsername(), nodeUser.getId(), e.getMessage());
			return "во время выбора аккаунта произошла ошибка, обратитесь к администратору системы.";
		}
	}
	
	@Override
	public UserState getType() {
		return UserState.ACCOUNT_LIST;
	}
	
}
