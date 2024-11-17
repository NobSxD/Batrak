package org.example.dao;

import org.example.entity.collect.Pair;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PairDAO extends JpaRepository<Pair, Long> {
}
