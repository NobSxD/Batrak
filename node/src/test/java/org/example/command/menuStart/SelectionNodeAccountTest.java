package org.example.command.menuStart;

import org.example.change.XChangeCommand;
import org.example.dao.NodeUserDAO;
import org.example.entity.NodeUser;
import org.example.service.ProcessServiceCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SelectionNodeAccountTest {
	SelectionChange selectionChange;

	@Mock
	ProcessServiceCommand processServiceCommand;

	@Mock
	NodeUserDAO nodeUserDAO;
	NodeUser nodeUser;

	private final XChangeCommand command;

	SelectionNodeAccountTest(XChangeCommand command) {
		this.command = command;
	}

	@BeforeEach
	void init(){
		nodeUser = new NodeUser();
		selectionChange = new SelectionChange(processServiceCommand, nodeUserDAO, command);
	}

	@Test
	void changeTest(){
		String send = selectionChange.send(nodeUser, "binance");
		System.out.println(send);

	}
}