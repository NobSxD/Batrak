package org.example.change;

import org.example.entity.NodeChange;
import org.example.entity.NodeUser;
import org.example.entity.enams.ChangeType;
import org.example.xchange.BasicChange;

public interface XChangeCommand {
	BasicChange init(NodeUser nodeUser);
	NodeChange getNodeChange(ChangeType changeType);
}
