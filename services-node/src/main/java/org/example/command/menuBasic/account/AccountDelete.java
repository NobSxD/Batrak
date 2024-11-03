package org.example.command.menuBasic.account;

import org.example.command.RoleProvider;
import org.example.entity.enams.Role;
import org.example.factory.account.NodeAccount;
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
public class AccountDelete implements Command, RoleProvider {
	private final NodeAccount nodeAccount;
	private final ProducerTelegramService producerTelegramService;
	@Override
	public String send(NodeUser nodeUser, String nameAccount) {
		try {
			Account account = nodeAccount.getAccount(nameAccount, nodeUser);
			if (account == null){
				return "Аккаунт не найден, обновите страницу";
			}
			nodeAccount.deleteFindId(account.getId());
			if (nodeUser.getAccount().getNameAccount().equals(nameAccount)){
				nodeUser.setAccount(null);
			}
			producerTelegramService.menuMain(account.getNameAccount() + " данный аккаунт был удален ", nodeUser.getChatId()); //TODO стоит расмотреть вернуть это значение а producerTelegramService удалить
			return "";
		} catch (NullPointerException e){
			log.error("аккаунт пользователя под id: {}, не найден. \n Имя аккаунта: {} \n Ошибка: {}", nodeUser.getChatId(), nameAccount, e.getMessage());
			return "Аккаунт " + nameAccount + " не найден";
		}catch (Exception e){
			log.error("Не известная ошибка в классе DeleteAccount: {}", e.getMessage());
			return "Произошла непредвиденная ошибка";
		}
	}

	@Override
	public UserState getType() {
		return UserState.ACCOUNT_DELETE;
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}
}
