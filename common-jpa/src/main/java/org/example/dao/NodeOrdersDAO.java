package org.example.dao;

import org.example.entity.NodeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeOrdersDAO extends JpaRepository<NodeOrder, Long> {
}
