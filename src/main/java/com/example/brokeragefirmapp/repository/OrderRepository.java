package com.example.brokeragefirmapp.repository;

import com.example.brokeragefirmapp.entity.Order;
import com.example.brokeragefirmapp.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween( Long customerId, LocalDateTime startDate, LocalDateTime endDate );

    List<Order> findByStatus( OrderStatus status );

    List<Order> findByCustomerId( Long customerId );

    // Find pending SELL orders where sell price <= buyer's price
    @Query( "SELECT o FROM Order o WHERE o.assetName = :assetName AND o.orderSide = 'SELL' AND o.status = 'PENDING' AND o.price <= :maxPrice" )
    List<Order> findPendingSellOrders( String assetName, BigDecimal maxPrice );

    // Find pending BUY orders where buy price >= seller's price
    @Query( "SELECT o FROM Order o WHERE o.assetName = :assetName AND o.orderSide = 'BUY' AND o.status = 'PENDING' AND o.price >= :minPrice" )
    List<Order> findPendingBuyOrders( String assetName, BigDecimal minPrice );
}
