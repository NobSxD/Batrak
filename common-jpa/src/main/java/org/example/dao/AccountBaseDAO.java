package org.example.dao;


import org.example.entity.NodeUser;
import org.example.entity.Account;
import org.example.entity.collect.ChangeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountBaseDAO extends JpaRepository<Account,Long> {
	Optional<Account> findByNameAccountAndChangeTypeAndNodeUser(String nameAccount, ChangeType type, NodeUser nodeUser);
	List<Account> findAllByChangeTypeAndNodeUser(ChangeType type, NodeUser nodeUser);
	boolean existsByChangeTypeAndNodeUser(ChangeType type, NodeUser nodeUser);

}
