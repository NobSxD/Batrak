package org.example.dao;

import org.example.entity.ConfigTrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsTradeDAO extends JpaRepository<ConfigTrade, Long> {
}
