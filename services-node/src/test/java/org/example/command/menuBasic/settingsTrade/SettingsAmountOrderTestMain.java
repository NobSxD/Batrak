package org.example.command.menuBasic.settingsTrade;

import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.entity.enams.state.UserState;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingsAmountOrderTestMain {
	NodeUser nodeUser;
	ConfigTrade configTrade;
	
	@BeforeEach
	void newSaveAmountOrder(){
		configTrade = new ConfigTrade();
		nodeUser = NodeUser.builder()
				.id(1L)
				.chatId(505077047L)
				.username("Сергей Тест")
				.firstName("Сергей")
				.lastName("Тест")
				.state(UserState.BASIC_STATE)
				.telegramUserId(505077047L)
				.tradeStartOrStop(false)
				.configTrade(configTrade)
				.build();
	}


	@Test
	void testExpectedException() {

		NumberFormatException thrown = Assertions.assertThrows(NumberFormatException.class, () -> {
			Integer.parseInt("One");
		}, "NumberFormatException was expected");

		Assertions.assertEquals("For input string: \"One\"", thrown.getMessage());
	}
}