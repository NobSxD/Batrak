package org.example.dao;


import org.example.entity.account.AccountMexc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMexcDAO extends AccountBaseDAO<AccountMexc>, JpaRepository<AccountMexc, Long> {
}
