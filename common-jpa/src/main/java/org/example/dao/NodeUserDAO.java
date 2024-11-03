package org.example.dao;


import java.util.List;
import java.util.Optional;

import org.example.entity.NodeUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeUserDAO extends JpaRepository<NodeUser, Long> {
	Optional<NodeUser> findByTelegramUserId(Long id);

	Optional<NodeUser> findById(Long id);
	Optional<NodeUser> findByUsername(String userName);

	Optional<NodeUser> findByEmail(String email);

	List<NodeUser> findByIsActiveTrue();
	List<NodeUser> findByIsActiveFalse();
}
