package org.example.dto;

import org.example.xchange.finance.CurrencyConverter;
import org.example.xchange.finance.FinancialCalculator;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;

public class MarketTradeDetails {
    private final Instrument instrument;
    private final BigDecimal coinAmount;
    private final double stepSell;
    private final double stepBay;
    private final int scale;
    private BigDecimal endPrice;
    private BigDecimal nextBay;
    private BigDecimal nexSell;
    private BigDecimal lastPrice;
    private String recentAction;
    private int countDeal;
    private int maxCountDeal;
    private BigDecimal sell;

    public MarketTradeDetails(Instrument instrument, BigDecimal coinAmount, double stepSell, double stepBay, int scale) {
        this.instrument = instrument;
        this.coinAmount = coinAmount;
        this.stepSell = stepSell;
        this.stepBay = stepBay;
        this.scale = scale;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public double getStepSell() {
        return stepSell;
    }

    public double getStepBay() {
        return stepBay;
    }

    public int getScale() {
        return scale;
    }

    public BigDecimal getEndPrice() {
        return CurrencyConverter.validUsd(endPrice);
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public BigDecimal getNextBay() {
        return CurrencyConverter.validUsd(nextBay);
    }

    public void setNextBay(BigDecimal nextBay) {
        this.nextBay = nextBay;
    }

    public BigDecimal getNexSell() {
        return CurrencyConverter.validUsd(nexSell);
    }

    public void setNexSell(BigDecimal nexSell) {
        this.nexSell = nexSell;
    }

    public BigDecimal getLastPrice() {
        return CurrencyConverter.validUsd(lastPrice);
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getRecentAction() {
        return recentAction;
    }

    public void setRecentAction(String recentAction) {
        this.recentAction = recentAction;
    }

    public int getCountDeal() {
        return countDeal;
    }

    public void setCountDeal(int countDeal) {
        this.countDeal = countDeal;
    }

    public int getMaxCountDeal() {
        return maxCountDeal;
    }

    public void setMaxCountDeal(int deposit, int amount) {
        this.maxCountDeal = deposit / amount;
    }

    public BigDecimal getSell() {
        return FinancialCalculator.increaseByPercentage(coinAmount, stepSell);
    }
}
