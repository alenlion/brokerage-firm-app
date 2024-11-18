package com.example.brokeragefirmapp.controller;

import com.example.brokeragefirmapp.dto.ApiResponse;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    @PreAuthorize( "hasRole('ADMIN') or #orderDTO.customerId == @userService.getCurrentUserId()" )
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder( @Valid @RequestBody OrderDTO orderDTO ) {
        OrderDTO createdOrder = orderService.createOrder( orderDTO );
        ApiResponse<OrderDTO> response = new ApiResponse<>( true, "Order created successfully", createdOrder );
        return ResponseEntity.status( HttpStatus.CREATED ).body( response );
    }

    @GetMapping
    @PreAuthorize( "hasRole('ADMIN') or #customerId == @userService.getCurrentUserId()" )
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listOrders(
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat( iso = DateTimeFormat.ISO.DATE ) LocalDate startDate,
            @RequestParam @DateTimeFormat( iso = DateTimeFormat.ISO.DATE ) LocalDate endDate ) {
        List<OrderDTO> orders = orderService.listOrders( customerId, startDate.atStartOfDay(), endDate.atTime( LocalTime.MAX ) );
        ApiResponse<List<OrderDTO>> response = new ApiResponse<>( true, "Orders retrieved successfully", orders );
        return ResponseEntity.ok( response );
    }

    @DeleteMapping( "/{orderId}" )
    @PreAuthorize( "hasRole('ADMIN') or @userService.isOrderOwner(#orderId)" )
    public ResponseEntity<ApiResponse<Void>> cancelOrder( @PathVariable Long orderId ) {

        orderService.cancelOrder( orderId );
        ApiResponse<Void> response = new ApiResponse<>( true, "Order canceled successfully", null );
        return ResponseEntity.ok( response );
    }
}

