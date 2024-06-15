package org.example.xchange.Binance.trede;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepthChartDemo {

	public static void main(String[] args) throws IOException {

		// Use the factory to get the version 1 Bitstamp exchange API using default settings
		Exchange bitstampExchange = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());

		// Interested in the public market data feed (no authentication)
		MarketDataService marketDataService = bitstampExchange.getMarketDataService();

		System.out.println("fetching data...");

		// Get the current orderbook
		Instrument instrument = CurrencyPair.BTC_USDT;
		OrderBook orderBook = marketDataService.getOrderBook(instrument, 25);

		System.out.println("received data.");

		System.out.println("plotting...");

		// Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).title("Bitstamp Order Book").xAxisTitle("BTC").yAxisTitle("USD").build();

		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

		// BIDS
		List<Number> xData = new ArrayList<>();
		List<Number> yData = new ArrayList<>();
		BigDecimal accumulatedBidUnits = new BigDecimal("0");
		for (LimitOrder limitOrder : orderBook.getBids()) {
			if (limitOrder.getLimitPrice().doubleValue() > 10) {
				xData.add(limitOrder.getLimitPrice());
				accumulatedBidUnits = accumulatedBidUnits.add(limitOrder.getLimitPrice());
				yData.add(accumulatedBidUnits);
			}
		}
		Collections.reverse(xData);
		Collections.reverse(yData);

		// Bids Series
		XYSeries series = chart.addSeries("bids", xData, yData);
		series.setMarker(SeriesMarkers.NONE);

		// ASKS
		xData = new ArrayList<>();
		yData = new ArrayList<>();
		BigDecimal accumulatedAskUnits = new BigDecimal("0");
		for (LimitOrder limitOrder : orderBook.getAsks()) {
			if (limitOrder.getLimitPrice().doubleValue() < 100000) {
				xData.add(limitOrder.getLimitPrice());
				accumulatedAskUnits = accumulatedAskUnits.add(limitOrder.getLimitPrice());
				yData.add(accumulatedAskUnits);
			}
		}

		// Asks Series
		series = chart.addSeries("asks", xData, yData);
		series.setMarker(SeriesMarkers.NONE);

		new SwingWrapper(chart).displayChart();

	}

}