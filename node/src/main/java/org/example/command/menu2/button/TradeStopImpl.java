package org.example.command.menu2.button;

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
