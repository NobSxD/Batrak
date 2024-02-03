package com.example.node.dao;

import com.example.node.entity.UserMain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMainDAO extends JpaRepository<UserMain, Long> {
    Optional<UserMain> findByTelegramUserId(Long id);

    Optional<UserMain> findById(Long id);

    Optional<UserMain> findByEmail(String email);
}
