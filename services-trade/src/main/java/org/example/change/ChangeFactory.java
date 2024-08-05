package org.example.change;

import org.example.entity.NodeUser;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.DTO.ChangeUser;
import org.example.xchange.change.BayBit.ByBitMainImpl;
import org.example.xchange.change.Binance.BinanceMainImpl;
import org.example.xchange.change.Mex.MexcMainImpl;

public class ChangeFactory {
	public static BasicChangeInterface createChange(NodeUser nodeUser){
		ChangeUser changeUser = ChangeUser.builder()
				.userName(nodeUser.getUsername())
				.apiKey(nodeUser.getAccount().getPublicApiKey())
				.secretKey(nodeUser.getAccount().getSecretApiKey())
				.botName(nodeUser.getAccount().getNameAccount())
				.build();
		BasicChangeInterface change = null;
		switch (nodeUser.getChangeType()){
			case Mexc -> change = new MexcMainImpl(changeUser);
			case Bybit -> change = new ByBitMainImpl(changeUser);
			case Binance -> change = new BinanceMainImpl(changeUser);
		}
		return change;
	}

}
