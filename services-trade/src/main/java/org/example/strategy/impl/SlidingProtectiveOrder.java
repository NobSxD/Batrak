package org.example.strategy.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ConfigTrade;
import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.example.strategy.impl.helper.TradeStatusManager;
import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.FundsExceededException;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Класс продукции со свойствами <b>maker</b> и <b>price</b>.
 *
 * @version 0.1
 * @autor Сергей Чесноков
 */

@Slf4j
public class SlidingProtectiveOrder extends StrategyBasic {

    public SlidingProtectiveOrder() {
        super(new TradeStatusManager());
    }

    public TradeStatusManager getTradeStatusManager() {
        return tradeStatusManager;
    }

    @Transactional
    public NodeOrder bay(NodeUser nodeUser) {
        // Получение списка ордеров в стакане
        OrderBook orderBook = basicChange.orderBooksLimitOrders(
                nodeUser.getConfigTrade().getDepthGlass(),
                nodeUser.getConfigTrade().getNamePair()
        );
        if (basicChange == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        try {
            latch = new CountDownLatch(1);
            List<LimitOrder> bids = orderBook.getBids();
            ConfigTrade configTrade = nodeUser.getConfigTrade();
            Instrument currencyPair = new CurrencyPair(configTrade.getNamePair());
            BigDecimal price = FinancialCalculator.maxAmount(bids);
            BigDecimal amount = CurrencyConverter.convertCurrency(price, nodeUser.getConfigTrade().getAmountOrder(),
                    configTrade.getScale());
            List<BigDecimal> priceAndAmount = List.of(price, amount);

            return processOrder(nodeUser, currencyPair, priceAndAmount, Order.OrderType.BID, true);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            e.printStackTrace();
            return handleGeneralException(nodeUser, e);
        }
    }

    @Transactional
    public NodeOrder sel(NodeUser nodeUser) {
        // Получение списка ордеров в стакане
        OrderBook orderBook = basicChange.orderBooksLimitOrders(
                nodeUser.getConfigTrade().getDepthGlass(),
                nodeUser.getConfigTrade().getNamePair()
        );
        if (basicChange == null) {
            throw new IllegalArgumentException("BasicChangeInterface cannot be null");
        }
        try {
            latch = new CountDownLatch(1);
            List<LimitOrder> asks = orderBook.getAsks();
            Instrument currencyPair = new CurrencyPair(order.getInstrument());
            BigDecimal price = CurrencyConverter.validUsd(FinancialCalculator.maxAmount(asks));
            BigDecimal amount = order.getOriginalAmount();
            List<BigDecimal> priceAndAmount = List.of(price, amount);

            return processOrder(nodeUser, currencyPair, priceAndAmount, Order.OrderType.ASK, false);
        } catch (FundsExceededException e) {
            return handleFundsExceededException(nodeUser, e);
        } catch (Exception e) {
            return handleGeneralException(nodeUser, e);
        }
    }


}
