package com.example.xchange.test;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.mexc.MEXCExchange;

public class MEXCDemoUtils {
	public static Exchange createExchange() {

		ExchangeSpecification exSpec = new MEXCExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("Test");
		exSpec.setApiKey("mx0vgl7OJs0RII138s");
		exSpec.setSecretKey("7927e977970041048e4aaf190eb62886");
		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
}
