package org.example.command.menu2.menuService;

import org.example.command.menu2.button.Menu2;
import org.example.entity.enums.MenuEnums2;
import org.example.entity.NodeUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class Menu2ServiceImpl implements Menu2Service {
	private final Map<MenuEnums2, Menu2> meny2Map;

	public Menu2ServiceImpl(List<Menu2> menu2List) {
		this.meny2Map = menu2List.stream().collect(Collectors.toMap(Menu2::getType, Function.identity()));
	}
	@Override
	public String send(MenuEnums2 menuEnums2, NodeUser nodeUser){
		Menu2 menu2 = meny2Map.get(menuEnums2);
		return menu2.send(nodeUser);
	}

}
