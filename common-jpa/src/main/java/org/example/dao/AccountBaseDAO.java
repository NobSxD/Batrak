package org.example.dao;


import org.example.entity.NodeUser;
import org.example.entity.Account;
import org.example.entity.enams.ChangeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AccountBaseDAO extends JpaRepository<Account,Long> {
	Account findByNameAccountAndChangeTypeAndNodeUser(String nameAccount, ChangeType type, NodeUser nodeUser);
	List<Account> findAllByChangeTypeAndNodeUser(ChangeType type, NodeUser nodeUser);

}
