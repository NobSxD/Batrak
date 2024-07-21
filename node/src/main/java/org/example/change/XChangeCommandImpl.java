package org.example.change;

import org.example.change.xChange.XChangeMain;
import org.example.entity.NodeChange;
import org.example.entity.enams.ChangeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class XChangeCommandImpl implements XChangeCommand{
	private final Map<ChangeType, XChangeMain> typeMap;



	public XChangeCommandImpl(List<XChangeMain> commands) {
		this.typeMap = commands.stream().collect(Collectors.toMap(XChangeMain::getType, Function.identity()));

	}


	@Override
	public NodeChange getNodeChange(ChangeType changeType){
		XChangeMain xChangeMain = typeMap.get(changeType);
		return xChangeMain.getChange();
	}
}
