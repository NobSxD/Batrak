package org.example.command.menu2.SerringsTrade;

import lombok.RequiredArgsConstructor;
import org.example.command.Command;
import org.example.dao.NodeUserDAO;
import org.example.dao.SettingsTradeDAO;
import org.example.entity.NodeUser;
import org.example.entity.enams.SettingUpTrading;
import org.example.entity.enams.UserState;
import org.example.processServiceCommand.ProcessServiceCommand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepthGlass implements Command {
	private final NodeUserDAO nodeUserDAO;
	private final SettingsTradeDAO settingsTradeDAO;
	private final ProcessServiceCommand processServiceCommand;
	@Override
	public String send(NodeUser nodeUser, String text) {
		try{
			SettingUpTrading settingUpTrading = SettingUpTrading.fromValue(text);
			 if (settingUpTrading != null && settingUpTrading.equals(SettingUpTrading.DepthGlass)) {
				 nodeUserDAO.save(nodeUser);
				 return "Укажите количество ордеров в стакане";
			}
			else if (settingUpTrading != null && ! settingUpTrading.equals(SettingUpTrading.DepthGlass)) {
				int count = Integer.parseInt(text);
				nodeUser.getSettingsTrade().setDepthGlass(count);
				settingsTradeDAO.save(nodeUser.getSettingsTrade());
				nodeUserDAO.save(nodeUser);
				processServiceCommand.menu3TradeSettings("Уровень глубины рынка  успешно изменен. Новая глубины рынка: " + text, nodeUser.getChatId());
			}
			return "";
		}catch (Exception e){
			System.out.println(e.getMessage());
			return "Ведите число";
		}
	}

	@Override
	public UserState getType() {
		return UserState.DEPTH_GLASS;
	}
}
