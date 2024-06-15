package org.example.service;

import org.example.entity.NodeUser;
import org.example.xchange.BasicChangeInterface;

public interface Strategy {
	String slidingProtectiveOrder(NodeUser nodeUser, BasicChangeInterface basicChange);
}
