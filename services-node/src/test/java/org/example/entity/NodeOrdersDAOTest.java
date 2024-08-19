package org.example.entity;

import jakarta.persistence.EntityManager;
import org.example.dao.NodeOrdersDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class NodeOrdersDAOTest {
	
	@Autowired
	private NodeOrdersDAO nodeOrdersDAO;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testFindAllOrdersFromStartTimeAndNodeUser() {
		// Arrange: создайте тестового пользователя и заказы
		NodeUser testUser = new NodeUser();
		testUser.setUsername("testuser");
		entityManager.persist(testUser);

		NodeOrder order1 = new NodeOrder();
		order1.setTimestamp(LocalDateTime.now().minusDays(1));
		order1.setNodeUser(testUser);
		entityManager.persist(order1);

		NodeOrder order2 = new NodeOrder();
		order2.setTimestamp(LocalDateTime.now());
		order2.setNodeUser(testUser);
		entityManager.persist(order2);
		
		NodeOrder order3 = new NodeOrder();
		order3.setTimestamp(LocalDateTime.now().minusDays(3));
		order3.setNodeUser(testUser);
		entityManager.persist(order3);

		// Act: вызовите метод вашего репозитория
		LocalDateTime startTime = LocalDateTime.now().minusDays(2);
		List<NodeOrder> orders = nodeOrdersDAO.findAllOrdersFromTimestampAndNodeUser(startTime, testUser);

		// Assert: проверьте результат
		assertNotNull(orders);
		assertEquals(2, orders.size());
	}

}