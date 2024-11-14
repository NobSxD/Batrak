package org.example.xchange;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderByInstrumentAndIdParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.ToString;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ToString
public abstract class BasicChange implements BasicChangeInterface, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BasicChange.class);

    protected Exchange exchange;


    public String placeLimitOrder(LimitOrder limitOrder) { //если false то мы не отпровляем ордер на биржу
        if (limitOrder == null) {
            throw new IllegalArgumentException("LimitOrder cannot be null");
        }
        try {
            TradeService tradeService = exchange.getTradeService();
            String orderId = tradeService.placeLimitOrder(limitOrder);

            logger.info(limitOrder.toString());
            return orderId;
        } catch (IOException e) {
            logger.error("Ошибка: {}, лимит ордер: {}", e.getMessage(), limitOrder);
            throw new RuntimeException(e);
        }
    }

    public String placeMarketOrder(MarketOrder marketOrder) { //если false то мы не отпровляем ордер на биржу
        if (marketOrder == null) {
            throw new IllegalArgumentException("MarketOrder cannot be null");
        }
        try {
            TradeService tradeService = exchange.getTradeService();
            String orderId = tradeService.placeMarketOrder(marketOrder);
            logger.info(marketOrder.toString());
            return orderId;
        } catch (IOException e) {
            logger.error("Ошибка: {}, маркет ордер: {}", e.getMessage(), marketOrder);
            throw new RuntimeException(e);
        }
    }


    public LimitOrder createOrder(Instrument currencyPair, List<BigDecimal> priceAndAmount, Order.OrderType orderType) {
        if (currencyPair == null) {
            throw new IllegalArgumentException("CurrencyPair cannot be null");
        }
        if (priceAndAmount == null) {
            throw new IllegalArgumentException("PriceAndAmount cannot be null");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("OrderType cannot be null");
        }
        return new LimitOrder(orderType, priceAndAmount.get(1), currencyPair, "", null, priceAndAmount.get(0));
    }

    public MarketOrder createMarketOrder(Order.OrderType orderType, BigDecimal coinAmount, BigDecimal price,
                                         int scale, Instrument currencyPair) {
        if (orderType == null) {
            throw new IllegalArgumentException("OrderType cannot be null");
        }
        if (coinAmount == null) {
            throw new IllegalArgumentException("Summa cannot be null");
        }
        if (currencyPair == null) {
            throw new IllegalArgumentException("Instrument cannot be null");
        }
        if (orderType.equals(Order.OrderType.BID)) {
            coinAmount = coinConvertBay(coinAmount, price, scale);
        }
        if (orderType.equals(Order.OrderType.ASK)) {
            coinAmount = coinConvertSel(coinAmount, price, scale);
        }

        return new MarketOrder(orderType, coinAmount, currencyPair, null, new Date());
    }

    public abstract BigDecimal coinConvertBay(BigDecimal amount, BigDecimal price, int scale);

    public abstract BigDecimal coinConvertSel(BigDecimal amount, BigDecimal price, int scale);

    public List<LimitOrder> createOrders(Instrument currencyPair, Order.OrderType orderType, List<List<BigDecimal>> orders) {
        List<LimitOrder> limitOrders = new ArrayList<>();
        for (List<BigDecimal> ask : orders) {
            checkArgument(
                    ask.size() == 2, "Expected a pair (price, amount) but got {0} elements.", ask.size());
            limitOrders.add(createOrder(currencyPair, ask, orderType));
        }
        return limitOrders;
    }

    public void checkArgument(boolean argument, String msgPattern, Object... msgArgs) {
        if (!argument) {
            throw new IllegalArgumentException(MessageFormat.format(msgPattern, msgArgs));
        }
    }

    public OrderBook orderBooksLimitOrders(Integer countLimitOrders, String pairName) {
        OrderBook orderBook = null;
        try {
            // Получение сервиса для работы с рыночными данными
            MarketDataService marketDataService = exchange.getMarketDataService();
            // Задание параметров запроса

            // Получение стакана ордеров по торговой паре BTC/USD
            Instrument currencyPair = new CurrencyPair(pairName);
            orderBook = marketDataService.getOrderBook(currencyPair, countLimitOrders);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return orderBook;
    }


    public void cancelOrder(String namePair, String idOrder) {
        try {
            Instrument instrument = new CurrencyPair(namePair);
            CancelOrderParams cancelOrderParams = new DefaultCancelOrderByInstrumentAndIdParams(instrument, idOrder);
            exchange.getTradeService().cancelOrder(cancelOrderParams);
        } catch (IOException e) {
            logger.error("ордер id: {}, IOException ошибка: {}", idOrder, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("ордер id: {}, Exception ошибка: {}", idOrder, e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public String accountInfo() {
        try {
            AccountInfo accountInfo = exchange.getAccountService().getAccountInfo();
            return accountInfo.toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}