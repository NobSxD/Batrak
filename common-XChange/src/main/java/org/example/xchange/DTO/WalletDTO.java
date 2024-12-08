package org.example.xchange.DTO;

import org.example.xchange.finance.CurrencyConverter;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;

public class WalletDTO {
    private final BigDecimal firstDeposit;
    private final CurrencyPair currencyPair;
    private BigDecimal deposit;
    private BigDecimal coin;
    private int countDelay;

    public WalletDTO(BigDecimal deposit, CurrencyPair currencyPair) {
        this.firstDeposit = deposit;
        this.deposit = deposit;
        this.coin = new BigDecimal(0);
        this.currencyPair = currencyPair;
    }

    public void set(BigDecimal amount, BigDecimal coin, Order.OrderType orderType){
        if (amount == null || coin == null || orderType == null) {
            throw new IllegalArgumentException("amount, coin, and orderType must not be null");
        }

        switch (orderType) {
            case BID -> {
                // Если BID, проверяем, хватает ли средств на депозите
                if (deposit.compareTo(amount) >= 0) {
                    deposit = deposit.subtract(amount); // уменьшаем депозит
                    this.coin = this.coin.add(coin);    // увеличиваем количество монет
                } else {
                    throw new IllegalArgumentException("Not enough funds in deposit for BID operation");
                }
            }
            case ASK -> {
                // Если ASK, проверяем, хватает ли монет для продажи
                if (this.coin.compareTo(coin) >= 0) {
                    this.coin = this.coin.subtract(coin); // уменьшаем количество монет
                    deposit = deposit.add(amount);        // добавляем к депозиту
                } else {
                    throw new IllegalArgumentException("Not enough coins for ASK operation");
                }
            }
            default -> throw new IllegalStateException("Unsupported order type: " + orderType);
        }
    }

    @Override
    public String toString() {
        return "Deposit : " + CurrencyConverter.validScale(deposit) + "$\n" +
                "Coin-Balance : " + coin + " " + currencyPair.base;

    }
    public String result(){
        deposit = deposit.subtract(firstDeposit);
        return "Deposit : " + deposit + "$\n" +
                "Coin-Balance : " + coin + " " + currencyPair.base;
    }

    public void addCountDelay(){
        countDelay++;
    }

    public int getCountDelay() {
        return countDelay;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public BigDecimal getCoin() {
        return coin;
    }
    public BigDecimal getCoin(BigDecimal currentPrice) {
        return coin.multiply(currentPrice);
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public BigDecimal getFirstDeposit() {
        return firstDeposit;
    }
}
