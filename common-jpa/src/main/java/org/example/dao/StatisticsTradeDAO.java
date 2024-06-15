package org.example.dao;

import org.example.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsTradeDAO extends JpaRepository<Statistics, Long> {
}
