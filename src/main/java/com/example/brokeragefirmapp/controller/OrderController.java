package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@RestController
@RequestMapping( "/api/orders" )
public class OrderController {

    private final OrderService orderService;

    public OrderController( OrderService orderService ) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize( "hasRole('ADMIN') or #orderDTO.customerId.toString() == principal" )
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder( @Valid @RequestBody OrderDTO orderDTO ) {
        OrderDTO createdOrder = orderService.createOrder( orderDTO );
        ApiResponse<OrderDTO> response = new ApiResponse<>( true, "Order created successfully", createdOrder );
        return ResponseEntity.status( HttpStatus.CREATED ).body( response );
    }

    @GetMapping
    @PreAuthorize( "hasRole('ADMIN') or #customerId.toString() == principal" )
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listOrders(
            @RequestParam Long customerId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate ) {
        List<OrderDTO> orders = orderService.listOrders( customerId, startDate, endDate );
        ApiResponse<List<OrderDTO>> response = new ApiResponse<>( true, "Orders retrieved successfully", orders );
        return ResponseEntity.ok( response );
    }

//    @DeleteMapping("/{orderId}")
//    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#orderId, principal)")
//    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
//        String username = SecurityUtils.getCurrentUsername();
//        orderService.cancelOrder(orderId, username);
//        ApiResponse<Void> response = new ApiResponse<>(true, "Order canceled successfully", null);
//        return ResponseEntity.ok(response);
//    }
}

