package org.example.service;

import org.example.entity.NodeChange;
import org.example.entity.enams.ChangeType;

import java.util.List;

public interface NodeChangeService {
	void saveNodeChange(ChangeType changeType, List<String> currencyPairs);
	NodeChange getNodeChange(ChangeType changeType);
}
