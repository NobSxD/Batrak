package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import org.example.entity.NodeOrder;
import org.example.entity.collect.ChangeType;
import org.knowm.xchange.instrument.Instrument;

public interface WebSocketChange {
	Observable<NodeOrder> getCurrencyRateStream();
	ChangeType getType();
	Observable<NodeOrder> exchangePair(Instrument instrument);
}
