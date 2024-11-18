package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.OrderCreateRequest;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

public interface OrderService {
    OrderDTO createOrder( OrderDTO orderDTO );

    void createOrder( OrderCreateRequest orderRequest );

    List<OrderDTO> listOrders( Long customerId, LocalDateTime startDate, LocalDateTime endDate );

    void cancelOrder( Long orderId );
    Order findOrderById( Long orderId );
}
