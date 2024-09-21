package org.example.strategy.impl;

import org.example.dao.NodeOrdersDAO;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.menu.MenuStrategy;
import org.example.service.ProcessServiceCommand;
import org.example.xchange.BasicChangeInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionsOfEvents  {
	
	private BasicChangeInterface basicChange;
	private final ProcessServiceCommand producerServiceExchange;
	private final NodeUserDAO nodeUserDAO;
	private final NodeOrdersDAO ordersDAO;
	

	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		
		return null;
	}
	

	public MenuStrategy getType() {
		return null;
	}
}
