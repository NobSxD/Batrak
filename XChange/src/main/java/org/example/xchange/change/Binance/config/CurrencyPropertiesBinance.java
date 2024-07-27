package org.example.xchange.change.Binance.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dao.NodeChangeDAO;
import org.example.dao.PairDAO;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;
import org.example.entity.enams.ChangeType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "currency-binance")
@Getter
@Setter
@RequiredArgsConstructor
public class CurrencyPropertiesBinance {
	private List<String> pairs;

	private final NodeChangeDAO nodeChangeDAO;

	private final PairDAO pairDAO;

	@PostConstruct
	@Transient
	public void saveNodeChange(){
		NodeChange nodeChange = nodeChangeDAO.findByChangeType(ChangeType.Binance)
				.orElseGet(() -> NodeChange.builder()
						.changeType(ChangeType.Binance)
						.build());
		// Удаление старых пар
		nodeChange.setPairs(new ArrayList<>());
		nodeChangeDAO.save(nodeChange); // Очищаем старые пары

		// Добавление новых пар
		for (String currencyPair : pairs) {
			Pair pair = pairDAO.findByPair(currencyPair)
					.orElseGet(() -> new Pair(currencyPair));
			pairDAO.save(pair);
			nodeChange.addPair(pair);
		}
		nodeChangeDAO.save(nodeChange);
	}

}
