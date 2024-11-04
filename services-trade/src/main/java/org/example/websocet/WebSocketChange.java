package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import org.knowm.xchange.instrument.Instrument;

import java.math.BigDecimal;

import org.example.entity.collect.ChangeType;

public interface WebSocketChange {
	ChangeType getType();
	Observable<BigDecimal> exchangePair(Instrument instrument);
}
