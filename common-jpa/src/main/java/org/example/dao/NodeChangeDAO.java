package org.example.dao;

import org.example.entity.NodeChange;
import org.example.entity.collect.ChangeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeChangeDAO extends JpaRepository<NodeChange, Long>{
	Optional<NodeChange> findByChangeType(ChangeType changeType);
}
