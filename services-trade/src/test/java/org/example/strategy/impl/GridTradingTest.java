package org.example.strategy.impl;

import org.example.dto.MarketTradeDetails;
import org.example.service.ProcessServiceCommand;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.websocet.WebSocketCommand;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.change.DemoChange.NoChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.dto.Order;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.entity.ConfigTrade;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;

import static org.example.entity.enams.state.UserState.BASIC_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    private GridTrading tradingStrategy;
    private MarketTradeDetails marketTradeDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        configTrade.setNamePair("BTC-USDT");
        configTrade.setDeposit(new BigDecimal(300));
        configTrade.setScale(5);
        configTrade.setStepSell(1);
        configTrade.setAmountOrder(new BigDecimal("11"));
        configTrade.setStepBay(1);
        configTrade.setEnableDemoTrading(true);
        nodeUser.setConfigTrade(configTrade);

        basicChange = new NoChange();
        tradingStrategy = new GridTrading(nodeUser, basicChange, nodeUserDAO, webSocketCommand, producerServiceExchange,nodeOrdersDAO);
        marketTradeDetails = tradingStrategy.getMarketTradeDetails();
    }


    private static List<BigDecimal> generateCurrentPrices(int count, BigDecimal initialPrice, BigDecimal minPrice, BigDecimal maxPrice) {
        List<BigDecimal> prices = new ArrayList<>();
        Random random = new Random();
        BigDecimal currentPrice = initialPrice;

        prices.add(currentPrice);

        for (int i = 1; i < count; i++) {
            // Генерируем случайное изменение в пределах от -2% до +2%
            double change = -0.02 + (0.04 * random.nextDouble()); // Изменение от -2% до +2%
            currentPrice = currentPrice.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(change))).setScale(2, BigDecimal.ROUND_HALF_UP);

            // Ограничиваем изменения диапазоном minPrice и maxPrice
            if (currentPrice.compareTo(minPrice) < 0) {
                currentPrice = minPrice;
            } else if (currentPrice.compareTo(maxPrice) > 0) {
                currentPrice = maxPrice;
            }

            prices.add(currentPrice);
        }

        return prices;
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
                new BigDecimal("60600")
        );

                // Списки для отслеживания сделок
        List<BigDecimal> buyPrices = new ArrayList<>();
        List<BigDecimal> sellPrices = new ArrayList<>();
        marketTradeDetails.setLastPrice(new BigDecimal("60000"));
        tradeStatusManager.startTrading();
        tradingStrategy.tradeStatusManager.newCountDownLatch(1);
        for (BigDecimal currentPrice : currentPrices) {
            NodeOrder nodeOrder = tradingStrategy.processPrice(currentPrice, nodeUser);
            if (nodeOrder != null) {
                System.out.println("Прайс %s , тип %s".formatted(nodeOrder.getLimitPrice(), nodeOrder.getType()));

                if (nodeOrder.getType().equals(Order.OrderType.BID.toString())) {
                    buyPrices.add(nodeOrder.getLimitPrice().setScale(0, RoundingMode.HALF_UP));
                } else{
                    sellPrices.add(nodeOrder.getLimitPrice().setScale(0, RoundingMode.HALF_UP));
                }
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
                new BigDecimal("60600")
                // другие цены выше или ниже на меньшее значение
        );

        // Проверка, что настоящие и ожидаемые списки покупателей одинаковы
        assertEquals(expectedBuyPrices, buyPrices);

        // Проверка, что настоящие и ожидаемые списки продавцов одинаковы
        assertEquals(expectedSellPrices, sellPrices);
    }

    @Test
    void testTradeStartRandom() {

        List<BigDecimal> currentPrices = generateCurrentPrices(150, new BigDecimal("60000"), new BigDecimal("50000"), new BigDecimal("65000"));

        // Списки для отслеживания сделок
        List<BigDecimal> buyPrices = new ArrayList<>();
        List<BigDecimal> sellPrices = new ArrayList<>();
        marketTradeDetails.setLastPrice(new BigDecimal("60000"));
        tradeStatusManager.startTrading();
        tradingStrategy.tradeStatusManager.newCountDownLatch(1);
        for (BigDecimal currentPrice : currentPrices) {
            NodeOrder nodeOrder = tradingStrategy.processPrice(currentPrice, nodeUser);
            if (nodeOrder != null) {
                System.out.println("Прайс %s , тип %s".formatted(nodeOrder.getLimitPrice(), nodeOrder.getType()));

                if (nodeOrder.getType().equals(Order.OrderType.BID.toString())) {
                    buyPrices.add(nodeOrder.getLimitPrice().setScale(0, RoundingMode.HALF_UP));
                } else{
                    sellPrices.add(nodeOrder.getLimitPrice().setScale(0, RoundingMode.HALF_UP));
                }
            }
        }
        System.out.println("buyPrices %s".formatted(buyPrices));
        System.out.println("sellPrices %s".formatted(sellPrices));
    }

    @Test
    void lastPrice(){
        BigDecimal endPrice = new BigDecimal("68000");
        BigDecimal currentPrice = new BigDecimal("67000");
        boolean b = endPrice.compareTo(currentPrice) <= 0;
        assertFalse(b);
    }
}