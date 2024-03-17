package org.example.command.change;

import org.example.entity.enums.ChangeEnums;

public interface ChangeServiceNode {
	Change change(ChangeEnums type);
}
