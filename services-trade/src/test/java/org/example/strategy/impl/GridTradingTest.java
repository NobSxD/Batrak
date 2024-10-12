package org.example.strategy.impl;

import org.exampel.crypto.CryptoUtils;
import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeUser;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.entity.enams.state.UserState.BASIC_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GridTradingTest {
    @Mock
    private TradeStatusManager tradeStatusManager;

    @Mock
    private NodeUserDAO nodeUserDAO;

    private NodeUser nodeUser;
    @Mock
    private WebSocketCommand webSocketCommand;
    BasicChangeInterface basicChange;

    @Mock
    ProcessServiceCommand producerServiceExchange;
    @Mock
    NodeOrdersDAO nodeOrdersDAO;
    private List<BigDecimal> buyLevels;

    private GridTrading tradingStrategy;
    ChangeUser changeUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        buyLevels = new ArrayList<>();
        nodeUser = NodeUser.builder()
                .chatId(1L)
                .telegramUserId(2L)
                .username("ser")
                .firstName("ser")
                .lastName("ser")
                .isActive(false)
                .state(BASIC_STATE)
                .build();
        ConfigTrade configTrade = new ConfigTrade();
        configTrade.setNodeUser(nodeUser);
        configTrade.setEnableDemoTrading(true);
        nodeUser.setConfigTrade(configTrade);
        CryptoUtils cryptoUtils = new CryptoUtils();
        changeUser = ChangeUser.builder()
                .botName("Bot")
                .apiKey(cryptoUtils.encryptMessage(System.getenv("pKey")))
                .secretKey(cryptoUtils.encryptMessage(System.getenv("sKey")))
                .build();
        basicChange = new BinanceMainImpl(changeUser);
        tradingStrategy = new GridTrading(nodeUser, basicChange, nodeUserDAO, webSocketCommand, producerServiceExchange,nodeOrdersDAO);
    }

    @Test
    void testTradeStart() {
        List<BigDecimal> currentPrices = List.of(
                new BigDecimal("60000"),
                new BigDecimal("59400"), // снизилась на 1%, покупка по 59,400
                new BigDecimal("59400"), // взятие по уже купленной цене
                new BigDecimal("60000"), // значительно выше покупки, но ниже продажи, возможно, в будущем
                new BigDecimal("60030"), // чуть больше необходимой цены для продажи
                new BigDecimal("60060"),
                new BigDecimal("59703"),
                new BigDecimal("60300")
        );

        // Списки для отслеживания сделок
        List<BigDecimal> buyPrices = new ArrayList<>();
        List<BigDecimal> sellPrices = new ArrayList<>();

        for (BigDecimal currentPrice : currentPrices) {
            if (tradingStrategy.shouldBuy(currentPrice)) {
                tradeStatusManager.buy();
                tradingStrategy.executeBuyOrder(currentPrice, nodeUser);
                buyPrices.add(currentPrice);
                System.out.println("buy: " + currentPrice);
            } else if (tradingStrategy.shouldSell(currentPrice)) {
                tradeStatusManager.sell();
                tradingStrategy.executeSellOrder(currentPrice, nodeUser);
                sellPrices.add(currentPrice);
                System.out.println("sell: " + currentPrice);
            }
        }

        // Ожидаемые покупные цены (снизилась не менее чем на 1%)
        List<BigDecimal> expectedBuyPrices = List.of(
                new BigDecimal("60000"),
                new BigDecimal("59400")
                // другие цены повторяются, снижения более 1% нет
        );

        // Ожидаемые цены продажи
        // Ожидаемые цены продажи (выросла не менее чем на 0.5%)
        List<BigDecimal> expectedSellPrices = List.of(
                new BigDecimal("60000"),
                new BigDecimal("60300")
                // другие цены выше или ниже на меньшее значение
        );

        // Проверка, что настоящие и ожидаемые списки покупателей одинаковы
        assertEquals(expectedBuyPrices, buyPrices);

        // Проверка, что настоящие и ожидаемые списки продавцов одинаковы
        assertEquals(expectedSellPrices, sellPrices);
    }
}