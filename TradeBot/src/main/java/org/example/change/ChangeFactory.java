package org.example.change;

import org.example.entity.NodeUser;
import org.example.xchange.BasicChangeInterface;
import org.example.xchange.BayBit.ByBitMainImpl;
import org.example.xchange.Binance.BinanceMainImpl;
import org.example.xchange.Mex.MexcMainImpl;

public class ChangeFactory {
	public static BasicChangeInterface createChange(NodeUser nodeUser){
		BasicChangeInterface change = null;
		switch (nodeUser.getChangeType()){
			case Mex -> change = new MexcMainImpl(nodeUser);
			case Baibit -> change = new ByBitMainImpl(nodeUser);
			case Binance -> change = new BinanceMainImpl(nodeUser);
		}
		return change;
	}

}
