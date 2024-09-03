package org.example.service;

import org.example.entity.NodeUser;

public interface ProducerXChangeService {
	void startTread(NodeUser nodeUser);
	
	void cancelTread(NodeUser nodeUser);
}
