package org.example.command.menuBasic.account;

import org.example.button.ButtonLabelManager;
import org.example.factory.account.NodeAccount;
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
			if (!nodeAccount.existsByNodeUser(nodeUser)) {
				return "У вас нет аккаунта.";
			}
			List<Account> accounts = nodeAccount.getAccounts(nodeUser);
			if (text.equals(ButtonLabelManager.deleteAccount)) {
				nodeUser.setState(UserState.ACCOUNT_DELETE);
				producerTelegramService.accountsMenu(accounts, "Выберите аккаунт для удаления", nodeUser.getChatId());
				return "";
			}
			if (text.equals(ButtonLabelManager.choiceAccount)){
				nodeUser.setState(UserState.ACCOUNT_SELECT);
				producerTelegramService.accountsMenu(accounts, "Выберите аккаунт для торговли", nodeUser.getChatId());
				return "";
			}
			log.error("Пользаватель {}, пытался выбрать или удалить аккаунт. text= {}", nodeUser.getId(), text);
			return "Аккаунт не выбран и аккаунт не удален, пожалуйста повторите попытку или введите команду /start";
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
