package org.example.dao;


import org.example.entity.NodeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeUserDAO extends JpaRepository<NodeUser, Long> {
	Optional<NodeUser> findByTelegramUserId(Long id);

	Optional<NodeUser> findById(Long id);

	Optional<NodeUser> findByEmail(String email);
}
