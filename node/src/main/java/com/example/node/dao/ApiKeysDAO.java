package com.example.node.dao;

import com.example.node.entity.ApiKeys;
import com.example.node.entity.UserMain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeysDAO extends JpaRepository<ApiKeys, Long> {
    ApiKeys findByUserMainAndNameChange(UserMain userMain, String nameChange);
}
