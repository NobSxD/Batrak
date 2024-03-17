package org.example.dao;


import org.example.entity.account.AccountBinance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBinanceDAO extends AccountBaseDAO<AccountBinance>, JpaRepository<AccountBinance, Long> {
}
