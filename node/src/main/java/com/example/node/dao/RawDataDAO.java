package com.example.node.dao;

import com.example.node.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
