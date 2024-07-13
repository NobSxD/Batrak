package org.example.controller;

import org.example.xchange.change.Binance.Binance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeController {
	private final Binance exchange;

	public ExchangeController(Binance exchange) {
		this.exchange = exchange;
	}

	@GetMapping("/setCurrencyPair")
	public String setCurrencyPair(@RequestParam String pair) {
		try {
			exchange.setCurrencyPair(pair);
			return "Current Currency Pair: " + exchange.getCurrencyPair();
		} catch (Exception e) {
			return "Error";
		}
	}
	@GetMapping("/getCurrencyPair")
	public String getCurrencyPair() {

		try {
			return "Current Currency Pair: " + exchange.getCurrencyPair();
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}
}
