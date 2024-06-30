package org.example.command.menuMain.SerringsTrade;

import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.entity.enams.UserState;
import org.example.service.ProcessServiceCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AmountOrderTest {
	AmountOrder amountOrder;
	AmountOrder.SaveAmountOrder saveAmountOrder;
	NodeUser nodeUser;

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
		nodeUser = NodeUser.builder()
				.id(1L)
				.changeType(ChangeType.Binance)
				.chatId(505077047L)
				.username("Сергей Тест")
				.firstName("Сергей")
				.lastName("Тест")
				.state(UserState.SETTINGS_SAVE_AMOUNT_ORDER)
				.telegramUserId(505077047L)
				.tradeStartOrStop(false)
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
	void testExpectedException() {

		NumberFormatException thrown = Assertions.assertThrows(NumberFormatException.class, () -> {
			Integer.parseInt("One");
		}, "NumberFormatException was expected");

		Assertions.assertEquals("For input string: \"One\"", thrown.getMessage());
	}
}