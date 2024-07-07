package org.example.change;


import org.example.entity.NodeUser;

public interface Change {
	void tradeStart(NodeUser nodeUser);
	void infoAccount(NodeUser nodeUser);
	void infoBalanceAccount(NodeUser nodeUser);
	void cancelAllOrder(NodeUser nodeUser);

}
