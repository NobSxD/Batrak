package org.example.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entity.NodeOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodeOrdersDAO extends JpaRepository<NodeOrder, Long> {
    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :lastStartTread AND o.nodeUser.id = :nodeUserId")
    List<NodeOrder> findAllOrdersFromTimestampAndNodeUserId(@Param("lastStartTread") LocalDateTime lastStartTread, @Param("nodeUserId") Long nodeUserId);

    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :today AND o.nodeUser.id = :nodeUserId AND o.checkReal = :isReal")
    List<NodeOrder> findAllOrdersToday(@Param("today") LocalDateTime today, @Param("nodeUserId") Long nodeUserId,  @Param("isReal") boolean isReal);

    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :startOfWeek AND o.nodeUser.id = :nodeUserId AND o.checkReal = :isReal")
    List<NodeOrder> findAllOrdersThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("nodeUserId") Long nodeUserId,  @Param("isReal") boolean isReal);

    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :startOfMonth AND o.nodeUser.id = :nodeUserId AND o.checkReal = :isReal")
    List<NodeOrder> findAllOrdersThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("nodeUserId") Long nodeUserId,  @Param("isReal") boolean isReal);

    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :startOfHalfYear AND o.nodeUser.id = :nodeUserId AND o.checkReal = :isReal")
    List<NodeOrder> findAllOrdersThisHalfYear(@Param("startOfHalfYear") LocalDateTime startOfHalfYear, @Param("nodeUserId") Long nodeUserId,  @Param("isReal") boolean isReal);

    @Query("SELECT o FROM NodeOrder o WHERE o.timestamp >= :startOfYear AND o.nodeUser.id = :nodeUserId AND o.checkReal = :isReal")
    List<NodeOrder> findAllOrdersThisYear(@Param("startOfYear") LocalDateTime startOfYear, @Param("nodeUserId") Long nodeUserId, @Param("isReal") boolean isReal);

}
