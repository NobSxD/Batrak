package org.example.service.impl;

import org.example.change.Change;
import org.example.entity.NodeUser;
import org.example.service.MainServiceTradeBot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainServiceTradeBotImpl implements MainServiceTradeBot {
	private final Change change;
	@Override
	public void startORStopTrade(NodeUser nodeUser) {
		change.instance(nodeUser);
	}
}
