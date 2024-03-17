package org.example.command.menu2.impl;

import org.example.command.menu2.Menu2;
import org.example.entity.enums.MenuEnums2;
import org.example.entity.NodeUser;
import org.springframework.stereotype.Component;

@Component
public class TradeStopImpl implements Menu2 {
	@Override
	public String send(NodeUser nodeUser) {
		return "TradeStopImpl";
	}

	@Override
	public MenuEnums2 getType() {
		return MenuEnums2.TradeStop;
	}
}
