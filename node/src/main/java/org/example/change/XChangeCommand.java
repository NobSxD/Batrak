package org.example.change;

import org.example.entity.NodeChange;
import org.example.entity.enams.ChangeType;
import org.example.xchange.BasicChange;

public interface XChangeCommand {
	BasicChange init(ChangeType changeType);
	NodeChange getNodeChange(ChangeType changeType);
}
