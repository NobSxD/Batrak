package org.example.command.menu2.button;


import org.example.entity.enums.MenuEnums2;
import org.example.entity.NodeUser;

public interface Menu2 {

	String send(NodeUser nodeUser);
	MenuEnums2 getType();
}
