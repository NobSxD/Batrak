package org.example.change.xChange;

import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.xchange.BasicChange;

public interface XChangeMain {
	BasicChange init(NodeUser nodeUser);
	ChangeType getType();
}
