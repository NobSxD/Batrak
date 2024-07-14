package org.example.dao;

import org.example.entity.collect.Pair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PairDAO extends JpaRepository<Pair, Long> {
	Optional<Pair> findByPair(String pair);
}
