package com.example.brokeragefirmapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@RestController
@RequestMapping("/api/orders")
public class OrderController {

//    @Autowired
//    private OrderService orderService;
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN') or #orderDTO.customerId.toString() == principal")
//    public ResponseEntity<ApiResponse<OrderDTO>> createOrder( @Valid @RequestBody OrderDTO orderDTO) {
//        String username = SecurityUtils.getCurrentUsername();
//        OrderDTO createdOrder = orderService.createOrder(orderDTO, username);
//        ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Order created successfully", createdOrder);
//        return ResponseEntity.status( HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN') or #customerId.toString() == principal")
//    public ResponseEntity<ApiResponse<List<OrderDTO>>> listOrders(
//            @RequestParam Long customerId,
//            @RequestParam LocalDateTime startDate,
//            @RequestParam LocalDateTime endDate) {
//        List<OrderDTO> orders = orderService.listOrders(customerId, startDate, endDate);
//        ApiResponse<List<OrderDTO>> response = new ApiResponse<>(true, "Orders retrieved successfully", orders);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{orderId}")
//    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#orderId, principal)")
//    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
//        String username = SecurityUtils.getCurrentUsername();
//        orderService.cancelOrder(orderId, username);
//        ApiResponse<Void> response = new ApiResponse<>(true, "Order canceled successfully", null);
//        return ResponseEntity.ok(response);
//    }
}

