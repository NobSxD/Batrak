package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;

public interface WebSocketCommand {
	Observable<BigDecimal> getCurrencyRateStream();
}
