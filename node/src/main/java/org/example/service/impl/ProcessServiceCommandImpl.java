package org.example.service.impl;

import lombok.Data;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.Account;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.entity.Statistics;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerService producerService;
	private final SettingsTradeDAO settingsTradeDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;


	@Override
	public void menu1ChoosingAnExchange(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerChangeEnumsButton(sendMessage);
	}


	@Override
	public void menu2Selection(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuEnumsButton(sendMessage);
	}

	@Override
	public void menu3TradeSettings(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuTradeEnumsButton(sendMessage);

	}
	@Override
	public void listStrategy(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuListStrategy(sendMessage);

	}

	@Override
	public void listAccount(List<Account> accounts, String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerMenuListAccount(accounts,sendMessage);

	}


	@Override
	public String helpAccount() {
		return """
				Выбирите действие:
				/infoBalance - информация о балансе;
				/pair - выбор пары;
				/cancel - отмена выполнения текущей команды;
				""";

	}

	@Override
	public void sendAnswer(String output, Long chatId) {
		var sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerAnswer(sendMessage);
	}

	@Override
	public boolean isNotAllowToSendContent(Long chatId, NodeUser appUser) {
		var userState = appUser.getState();
		if (! appUser.getIsActive()) {
			var error = "Зарегистрируйтесь или активируйте " + "свою учетную запись для загрузки контента.";
			sendAnswer(error, chatId);
			return true;
		} else if (! BASIC_STATE.equals(userState)) {
			var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
			sendAnswer(error, chatId);
			return true;
		}
		return false;
	}
	@Override
	public NodeUser findOrSaveAppUser(Update update) {

		var telegramUser = update.getMessage() == null ? update.getCallbackQuery().getFrom() : update.getMessage().getFrom();
		var appUserOpt = nodeUserDAO.findByTelegramUserId(telegramUser.getId());
		if (appUserOpt.isEmpty()) {
			var settingsTrade = new ConfigTrade();
			var statistic = new Statistics();
			NodeUser transientAppUser = NodeUser.builder()
					.telegramUserId(telegramUser.getId())
					.username(telegramUser.getUserName())
					.firstName(telegramUser.getFirstName())
					.lastName(telegramUser.getLastName())
					.isActive(false).state(BASIC_STATE)
					.build();

			settingsTrade.setNodeUser(transientAppUser);
			statistic.setNodeUser(transientAppUser);

			transientAppUser.setStatistics(statistic);
			transientAppUser.setConfigTrade(settingsTrade);
			statisticsTradeDAO.save(statistic);
			settingsTradeDAO.save(settingsTrade);
			return nodeUserDAO.save(transientAppUser);
		}
		return appUserOpt.get();
	}

	@Override
	public void startTread(NodeUser nodeUser) {
		producerService.startTread(nodeUser);
	}


}
