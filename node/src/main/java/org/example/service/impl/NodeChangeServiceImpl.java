package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.NodeChangeDAO;
import org.example.dao.PairDAO;
import org.example.entity.NodeChange;
import org.example.entity.collect.Pair;
import org.example.entity.enams.ChangeType;
import org.example.service.NodeChangeService;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeChangeServiceImpl implements NodeChangeService {

	private final NodeChangeDAO nodeChangeDAO;

	private final PairDAO pairDAO;

	@Transient
	public void saveNodeChange(ChangeType changeType, List<String> currencyPairs){
		new NodeChange();
		NodeChange nodeChange = nodeChangeDAO.findByChangeType(changeType)
				.orElseGet(() -> NodeChange.builder()
						.changeType(changeType)
						.build());
		// Удаление старых пар
		nodeChange.setPairs(new ArrayList<>());
		nodeChangeDAO.save(nodeChange); // Очищаем старые пары

		// Добавление новых пар
		for (String currencyPair : currencyPairs) {
 			Pair pair = pairDAO.findByPair(currencyPair)
					.orElseGet(() -> new Pair(currencyPair));
			pairDAO.save(pair);
			nodeChange.addPair(pair);
		}
		nodeChangeDAO.save(nodeChange);
	}

	@Override
	public NodeChange getNodeChange(ChangeType changeType) {
		return nodeChangeDAO.findByChangeType(changeType).orElse(null);
	}

}
