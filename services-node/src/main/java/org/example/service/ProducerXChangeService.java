package org.example.service;

import org.example.entity.NodeUser;

public interface ProducerXChangeService {
	void startTrade(NodeUser nodeUser);
	void stopTrade(NodeUser nodeUser);
	void infoAccount(NodeUser nodeUser);
	void cancelTread(NodeUser nodeUser);
	void stateTrade(NodeUser nodeUser);
}
