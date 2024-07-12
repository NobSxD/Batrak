package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.change.Change;
import org.example.entity.NodeUser;
import org.example.service.MainServiceTradeBot;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainServiceTradeBotTradeBotImpl implements MainServiceTradeBot {
	private final Change change;
	@Override
	public void startORStopTrade(NodeUser nodeUser) {
		change.instance(nodeUser);
	}
	public void infoAccount(NodeUser nodeUser){

	}
}
