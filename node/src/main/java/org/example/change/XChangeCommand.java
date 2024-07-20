package org.example.change;

import org.example.entity.NodeChange;
import org.example.entity.enams.ChangeType;

public interface XChangeCommand {
	NodeChange getNodeChange(ChangeType changeType);
}
