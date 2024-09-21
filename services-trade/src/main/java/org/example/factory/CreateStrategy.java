package org.example.factory;

import org.example.entity.NodeUser;
import org.example.strategy.Strategy;
import org.example.strategy.impl.SlidingProtectiveOrder;

public class CreateStrategy {
    public static Strategy createStrategy(NodeUser nodeUser){
        Strategy strategy = null;
        switch (nodeUser.getConfigTrade().getStrategy()){
            case SlidingProtectiveOrder -> strategy = new SlidingProtectiveOrder();
        }
        return strategy;
    }
}
