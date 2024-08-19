package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import org.example.entity.NodeOrder;
import org.example.entity.enams.ChangeType;

public interface WebSocketChange {
	Observable<NodeOrder> getCurrencyRateStream();
	ChangeType getType();
}
