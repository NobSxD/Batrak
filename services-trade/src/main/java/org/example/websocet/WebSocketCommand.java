package org.example.websocet;

import io.reactivex.rxjava3.core.Observable;
import org.example.entity.NodeOrder;

public interface WebSocketCommand {
	Observable<NodeOrder> getCurrencyRateStream();
}
