package org.example.account;

import org.example.entity.NodeUser;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;


public interface AccountXchange {
	Exchange instance(NodeUser nodeUser, BaseExchange baseExchange);
}
