package org.example.dao;


import org.example.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccountBaseDAO<T extends Account> extends JpaRepository<T,Long> {
	T findByNameChange(String nameChange);
	//T findBySecretApiKey(String key);
}
