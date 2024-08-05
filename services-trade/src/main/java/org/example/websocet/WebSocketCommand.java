package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import org.example.xchange.DTO.LimitOrderMain;

public interface WebSocketCommand {
	Observable<LimitOrderMain> getCurrencyRateStream();
}
