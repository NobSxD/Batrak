package org.example.command.menuMain.SerringsTrade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.entity.NodeUser;
import org.example.entity.enams.UserState;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static org.example.entity.enams.UserState.INFO_SETTINGS;

@Component
@RequiredArgsConstructor
@Slf4j
public class InfoSettings implements Command {

	@Override
	public String send(NodeUser nodeUser, String text) {
		try {
			String nameChange = nodeUser.getChangeType().name();
			BigDecimal amountOrder = nodeUser.getConfigTrade().getAmountOrder();
			String pair = nodeUser.getConfigTrade().getNamePair();
			int depthGlass = nodeUser.getConfigTrade().getDepthGlass();
			StringBuilder settings = new StringBuilder()
					.append("Текущие настройки: \n")
					.append("биржа - ").append(nameChange).append("\n")
					.append("сумма ордера в $ ").append(amountOrder).append("\n")
					.append("Валютная пара ").append(pair).append("\n")
					.append("Глубина ордеров в стакане ").append(depthGlass).append("\n");
			return settings.toString();
		}catch (Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			return "Во время запроса данных о ваших настройках произошла неизвестная ошибка " + e.getMessage();
		}
	}

	@Override
	public UserState getType() {
		return INFO_SETTINGS;
	}
}
