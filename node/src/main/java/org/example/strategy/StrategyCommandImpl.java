package org.example.strategy;

import org.example.entity.NodeUser;
import org.example.entity.enams.StrategyEnams;
import org.example.xchange.BasicChangeInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StrategyCommandImpl implements StrategyCommand{

	private final Map<StrategyEnams, StrategyTrade> stateMap;


	public StrategyCommandImpl(List<StrategyTrade> commands) {
		this.stateMap = commands.stream().collect(Collectors.toMap(StrategyTrade::getType, Function.identity()));

	}

	@Override
	public String trade(NodeUser nodeUser, BasicChangeInterface basicChange) {
		StrategyTrade command = stateMap.get(nodeUser.getSettingsTrade().getStrategy());
		return command.trade(nodeUser, basicChange);
	}
}
