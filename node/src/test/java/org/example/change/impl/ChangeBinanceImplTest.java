package org.example.change.impl;

import org.example.change.Change;
import org.example.dao.NodeUserDAO;
import org.example.dao.StatisticsTradeDAO;
import org.example.entity.NodeUser;
import org.example.xchange.BasicChangeInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangeBinanceImplTest {
	private Change tradeService;


	@Mock
	StatisticsTradeDAO statisticsTradeDAO;

	@Mock
	private NodeUserDAO nodeUserDAO;

	@Mock
	private BasicChangeInterface binance;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		//tradeService = new ChangeBinanceImpl(accountBinanceDAO,producerServiceExchange,nodeUserDAO,statisticsTradeDAO);
	}

	@Test
	public void testTradeStart() {
		NodeUser nodeUser = new NodeUser();
		when(nodeUserDAO.findById(anyLong())).thenReturn(java.util.Optional.of(nodeUser));

		//String result = tradeService.tradeStart(nodeUser);

		verify(nodeUserDAO).save(nodeUser);
		//assertEquals("Трейд запущен, начинаю искать точку входа", result);
	}

	@Test
	public void testTradeStop() {

		NodeUser nodeUser = new NodeUser();

		//String result = tradeService.tradeStop(nodeUser);

		verify(nodeUserDAO).save(nodeUser);
		//assertEquals("Трейдинг остановлен", result);
	}

}