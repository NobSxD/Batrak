package org.example.dao;


import org.example.entity.NodeUser;
import org.example.entity.Account;
import org.example.entity.enams.menu.MenuChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountBaseDAO extends JpaRepository<Account,Long> {
	Optional<Account> findByNameAccountAndMenuChangeAndNodeUser(String nameAccount, MenuChange type, NodeUser nodeUser);
	List<Account> findAllByMenuChangeAndNodeUser(MenuChange type, NodeUser nodeUser);

}
