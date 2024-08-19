package org.example.dao;

import org.example.entity.NodeOrder;
import org.example.entity.NodeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NodeOrdersDAO extends JpaRepository<NodeOrder, Long> {
	@Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :lastStartTread AND o.nodeUser = :nodeUser")
	List<NodeOrder> findAllOrdersFromTimestampAndNodeUser(@Param("lastStartTread") LocalDateTime lastStartTread, @Param("nodeUser") NodeUser nodeUser);
}
