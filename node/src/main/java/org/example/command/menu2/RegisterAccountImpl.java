package org.example.command.menu2;

import lombok.Data;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.account.Account;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceChangeCommands;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static org.example.entity.enams.UserState.*;


@Component
@Data
public class RegisterAccountImpl implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final PasswordEncoder passwordEncoder;
	private final ProcessServiceCommand processServiceCommand;
	private final ProcessServiceChangeCommands processServiceChangeCommands;





	@Override
	@Transactional
	public String send(NodeUser nodeUser, String text) {
		if (nodeUser.getMenuState() == null) {
			return accountName(nodeUser);
		} else if (nodeUser.getMenuState().equals(ACCOUNT_NAME)) {
			nodeUser.setAccount(processServiceChangeCommands.getChangeAccount());
			return accountName(nodeUser, text);
		} else if (nodeUser.getMenuState().equals(PUBLIC_API)) {
			return publicApi(nodeUser, text);
		} else if (nodeUser.getMenuState().equals(SECRET_API)) {
			return secretApi(nodeUser, text);
		}
		return "Регистрация не успешна";
	}

	public String accountName(NodeUser nodeUser) {
		nodeUser.setState(REGISTER_ACCOUNT);
		nodeUser.setMenuState(ACCOUNT_NAME);
		nodeUserDAO.save(nodeUser);
		return "Ведите имя аккаунта";
	}
	public String accountName(NodeUser nodeUser, String text) {
		if (text == null){
			return "Вы не ввели имя акканта, пожалуйста повторите ввод";
		}
		Account account = nodeUser.getAccount();
		account.setNameChange(text);
		nodeUser.setMenuState(PUBLIC_API);
		processServiceChangeCommands.saveAccount(account, nodeUser);
		nodeUserDAO.save(nodeUser);
		return "Ведите публичный ключ";
	}

	public String publicApi(NodeUser nodeUser, String text) { // пришло имя аккаунта
		if (text == null){
			return "Вы не ввели публичный ключ, пожалуйста повторите ввод";
		}
		String encode = passwordEncoder.encode(text);
		try {
			Account account = nodeUser.getAccount();
			nodeUser.setMenuState(SECRET_API);
			account.setPublicApiKey(encode);
			nodeUser.setAccount(account);
			nodeUserDAO.save(nodeUser);
			return "ведите секретный ключ";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			processServiceCommand.menu2Selection("Вам не удолось добавить аккаунт, так как не выбрана биржа, выбирите биржу и повторите снова - ", nodeUser.getChatId());
			nodeUser.setState(CANCEL);
			nodeUserDAO.save(nodeUser);
			return "Команда отменена!";
		}

	}

	public String secretApi(NodeUser nodeUser, String text) { // пришел публичный ключ
		if (text == null){
			return "Вы не ввели секретный ключ, пожалуйста повторите ввод";
		}
		String encode = passwordEncoder.encode(text);
		try {
			Account account = nodeUser.getAccount();
			account.setSecretApiKey(encode);
			nodeUser.setState(UserState.ACCOUNT_USER);
			nodeUser.setMenuState(null);
			nodeUser.setAccount(account);
			processServiceChangeCommands.registerAccount(nodeUser);
			processServiceCommand.menu2Selection("Вы успешго добавили аккаунт - " + account.getNameChange(), nodeUser.getChatId());
			nodeUserDAO.save(nodeUser);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			processServiceCommand.menu2Selection("Вам не удолось добавить аккаунт, так как не выбрана биржа, выбирите биржу и повторите снова - ", nodeUser.getChatId());
			nodeUser.setState(CANCEL);
			nodeUserDAO.save(nodeUser);
			return "Команда отменена!";
		}

		return "";
	}

	@Override
	public UserState getType() {
		return UserState.REGISTER_ACCOUNT;
	}




}
