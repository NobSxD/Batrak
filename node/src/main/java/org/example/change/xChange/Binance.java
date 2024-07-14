package org.example.change.xChange;

import lombok.RequiredArgsConstructor;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.service.NodeChangeService;
import org.example.xchange.BasicChange;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.example.xchange.change.Binance.config.CurrencyProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;
import java.util.List;

@Service
@RequiredArgsConstructor
@DiscriminatorValue("BINANCE")
public class Binance  implements XChangeMain {

	private final NodeChangeService nodeChangeService;
	private final CurrencyProperties currencyProperties;

	@PostConstruct
	private void init() {
		List<String> propertiesPairs = currencyProperties.getPairs();
		nodeChangeService.saveNodeChange(ChangeType.Binance, propertiesPairs);
	}

	@Override
	public BasicChange init(NodeUser nodeUser){
		return new BinanceMainImpl(nodeUser);

	}
	@Override
	public ChangeType getType(){
		return ChangeType.Binance;
	}

}
