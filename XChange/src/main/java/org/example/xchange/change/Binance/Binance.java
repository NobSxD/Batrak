package org.example.xchange.change.Binance;

import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;
import org.example.entity.enams.ChangeType;
import org.example.xchange.BasicChange;
import org.example.xchange.change.Binance.config.CurrencyProperties;
import org.example.xchange.change.XChangeMain;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Binance extends NodeChange implements XChangeMain {


	public Binance(CurrencyProperties currencyProperties) {
		List<String> propertiesPairs = currencyProperties.getPairs();
		List<Pair> pairs = new ArrayList<>();
		for (String pair: propertiesPairs) {
			Pair pair1 = new Pair();
			pair1.setPair(pair);
			pair1.setNodeChange(this);
			pairs.add(pair1);
		}
		this.pairs = pairs;
	}
	public void setCurrencyPair(String pair) throws Exception{
		if (pairs.contains(pair)){
			this.pair = pair;
		}else {
			throw new Exception("Invalid currency pair: " + pair);
		}
	}
	public String getCurrencyPair(){
		return pair;
	}
	@Override
	public BasicChange init(){
	//	return new BinanceMainImpl(nodeUser);
		return null;
	}
	@Override
	public ChangeType getType(){
		return ChangeType.Binance;
	}

}
