package com.orders.repository;

import com.orders.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders o " +
            "WHERE (:orderId is NULL OR o.orderId = :orderId) " +
            " AND (" +
            " (:startDate IS NULL AND :endDate IS NULL) OR (o.orderDate BETWEEN :startDate AND :endDate) " +
            "     )")
    List<User> findUsersWithOrdersByDate(
            @Param("orderId") Long orderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
