package org.example.command.menuMain.SerringsTrade;

import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

class AmountOrderTest {
	AmountOrder amountOrder;
	AmountOrder.SaveAmountOrder saveAmountOrder;
	NodeUser nodeUser;
	ConfigTrade configTrade;

	@Mock
	private NodeUserDAO nodeUserDAO;
	@Mock
	private SettingsTradeDAO settingsTradeDAO;
	@Mock
	private ProcessServiceCommand processServiceCommand;
	@BeforeEach
	void newSaveAmountOrder(){
		amountOrder = new AmountOrder(nodeUserDAO, settingsTradeDAO, processServiceCommand);
		saveAmountOrder = amountOrder.new SaveAmountOrder();
		configTrade = new ConfigTrade();
		nodeUser = NodeUser.builder()
				.id(1L)
				.changeType(ChangeType.Binance)
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
	void send() {
		String send = saveAmountOrder.send(nodeUser, "10");
		Assertions.assertEquals("Вы ввели число меньше 11, минимальный размер ордера 11", send);

		send = saveAmountOrder.send(nodeUser, "11.5454854");
		Assertions.assertEquals("null", send);

		send = saveAmountOrder.send(nodeUser, "не число");
		Assertions.assertEquals("Ведите пожалуйсто число формат 100.22", send);

	}
	@Test
	void sendNumberFormat(){
		saveAmountOrder.send(nodeUser, "1154.5454854");
		Assertions.assertEquals(nodeUser.getConfigTrade().getAmountOrder(), new BigDecimal("1154.55"));

		saveAmountOrder.send(nodeUser, "11.5454854");
		Assertions.assertEquals(nodeUser.getConfigTrade().getAmountOrder(), new BigDecimal("11.55"));

		saveAmountOrder.send(nodeUser, "169.14");
		Assertions.assertEquals(nodeUser.getConfigTrade().getAmountOrder(), new BigDecimal("169.14"));

		saveAmountOrder.send(nodeUser, "11");
		Assertions.assertEquals(nodeUser.getConfigTrade().getAmountOrder(), new BigDecimal("11.00"));

		saveAmountOrder.send(nodeUser, "5000.5000000");
		Assertions.assertEquals(nodeUser.getConfigTrade().getAmountOrder(), new BigDecimal("5000.50"));
	}

	@Test
	void checkState(){
		amountOrder.send(nodeUser, "");
		Assertions.assertEquals(nodeUser.getState(), UserState.SETTINGS_SAVE_AMOUNT_ORDER);
	}

	@Test
	void testExpectedException() {

		NumberFormatException thrown = Assertions.assertThrows(NumberFormatException.class, () -> {
			Integer.parseInt("One");
		}, "NumberFormatException was expected");

		Assertions.assertEquals("For input string: \"One\"", thrown.getMessage());
	}
}