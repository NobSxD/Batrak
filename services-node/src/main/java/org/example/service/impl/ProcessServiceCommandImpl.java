package org.example.service.impl;

import lombok.Data;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.entity.Statistics;
import org.example.service.ProcessServiceCommand;
import org.example.service.ProducerTelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.entity.enams.UserState.BASIC_STATE;


@Component
@Data
public class ProcessServiceCommandImpl implements ProcessServiceCommand {
	private final NodeUserDAO nodeUserDAO;
	private final ProducerTelegramService producerTelegramService;
	private final SettingsTradeDAO settingsTradeDAO;
	private final StatisticsTradeDAO statisticsTradeDAO;


	@Override
	public String helpAccount() {
		return """
				Выбирите действие:
				/start - выбор биржи 
				/main - вызвать главное меню
				/info_settings - посмотреть текущие настройки
				/cancel - отмена выполнения текущей команды;
				""";

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


}
