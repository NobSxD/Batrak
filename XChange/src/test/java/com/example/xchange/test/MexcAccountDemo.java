package com.example.xchange.test;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.service.account.AccountService;

import java.io.IOException;
import java.math.BigDecimal;

public class MexcAccountDemo {
	public static void main(String[] args) throws IOException {

		Exchange bitstamp =MEXCDemoUtils.createExchange();
		AccountService accountService = bitstamp.getAccountService();

		generic(accountService);

	}

	private static void generic(AccountService accountService) throws IOException {

		// Get the account information
		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println("AccountInfo as String: " + accountInfo.toString());

		String depositAddress = accountService.requestDepositAddress(Currency.BTC);
		System.out.println("Deposit address: " + depositAddress);

		String withdrawResult = accountService.withdrawFunds(Currency.BTC, new BigDecimal(1).movePointLeft(4), "XXX");
		System.out.println("withdrawResult = " + withdrawResult);
	}


}
