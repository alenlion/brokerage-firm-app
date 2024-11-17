package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.dto.OrderCreateRequest;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.entity.Order;
import com.example.brokeragefirmapp.enums.OrderSide;
import com.example.brokeragefirmapp.enums.OrderStatus;
import com.example.brokeragefirmapp.exception.InsufficientBalanceException;
import com.example.brokeragefirmapp.exception.OrderNotFoundException;
import com.example.brokeragefirmapp.mapper.OrderMapper;
import com.example.brokeragefirmapp.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final AssetService assetService;

    private final OrderMapper orderMapper;

    public OrderServiceImpl( OrderRepository orderRepository, AssetService assetService, OrderMapper orderMapper ) {
        this.orderRepository = orderRepository;
        this.assetService = assetService;
        this.orderMapper = orderMapper;
    }

    /**
     * Creates a new order and attempts to match it with existing orders.
     *
     * @param order The order to create.
     * @return The saved order.
     */

    public OrderDTO createOrder( OrderDTO order ) {
        if ( !assetService.assetIsExist( order.getAssetName() ) ) {
            throw new ResolutionException( "Asset not found" );
        }
        Long customerId = order.getCustomerId();
        String assetName = order.getAssetName();
        BigDecimal orderSize = order.getSize();
        BigDecimal orderPrice = order.getPrice();

        // Validate the customer's assets or funds before placing the order
        if ( order.getOrderSide() == OrderSide.SELL ) {
            // Seller must have enough of the asset to sell
            AssetDTO assetToSell = assetService.getAsset( customerId, assetName );
            if ( assetToSell == null || assetToSell.getUsableSize().compareTo( orderSize ) < 0 ) {
                throw new InsufficientBalanceException( "Not enough " + assetName + " to place the SELL order." );
            }
            // Lock the assets (reduce usableSize)
            assetToSell.setUsableSize( assetToSell.getUsableSize().subtract( orderSize ) );
            assetService.saveAsset( assetToSell );

        } else if ( order.getOrderSide() == OrderSide.BUY ) {
            // Buyer must have enough TRY to buy
            BigDecimal totalCost = orderPrice.multiply( orderSize );
            AssetDTO tryAsset = assetService.getAsset( customerId, "TRY" );
            if ( tryAsset == null || tryAsset.getUsableSize().compareTo( totalCost ) < 0 ) {
                throw new InsufficientBalanceException( "Not enough TRY to place the BUY order." );
            }
            // Lock the funds (reduce usableSize)
            tryAsset.setUsableSize( tryAsset.getUsableSize().subtract( totalCost ) );
            assetService.saveAsset( tryAsset );
        }

        // Set order status and creation date
        order.setStatus( OrderStatus.PENDING );
        order.setCreateDate( LocalDateTime.now() );
        order.setSize( orderSize );
        // Save the new order
        Order savedOrder = orderRepository.save( orderMapper.toEntity( order ) );

        // Attempt to match orders
        matchOrders( savedOrder );

        return orderMapper.toDTO( savedOrder );
    }

    @Override
    public void createOrder( OrderCreateRequest orderRequest ) {
        Order order = orderMapper.toEntity( orderRequest );
        order.setStatus( OrderStatus.PENDING );
        orderRepository.save( order );
    }

    /**
     * Attempts to match the given order with existing orders.
     *
     * @param newOrder The order to match.
     */
    public void matchOrders( Order newOrder ) {
        List<Order> potentialMatches;
        newOrder.setOrderSize( newOrder.getSize() );
        if ( newOrder.getOrderSide() == OrderSide.BUY ) {
            // Find pending SELL orders for the same asset where sell price <= buy price
            potentialMatches = orderRepository.findPendingSellOrders(
                    newOrder.getAssetName(), newOrder.getPrice() );

            // Sort by price ascending (lowest sell price first), then by time
            potentialMatches.sort( Comparator.comparing( Order::getPrice )
                    .thenComparing( Order::getCreateDate ) );

        } else {
            // SELL order
            // Find pending BUY orders for the same asset where buy price >= sell price
            potentialMatches = orderRepository.findPendingBuyOrders( newOrder.getAssetName(), newOrder.getPrice() );

            // Sort by price descending (highest buy price first), then by time
            potentialMatches.sort( Comparator.comparing( Order::getPrice ).reversed()
                    .thenComparing( Order::getCreateDate ) );
        }

        for ( Order matchingOrder : potentialMatches ) {
            if ( newOrder.getStatus() != OrderStatus.PENDING ) {
                break; // The new order has been fully matched
            }

            // Check price compatibility
            if ( !isPriceCompatible( newOrder, matchingOrder ) ) {
                continue; // Skip incompatible orders
            }

            // Determine trade size
            BigDecimal tradeSize = newOrder.getSize().min( matchingOrder.getSize() );
            BigDecimal executionPrice;
            // Determine execution price
            if ( newOrder.getOrderSide() == OrderSide.BUY ) {
                executionPrice = matchingOrder.getPrice();
            } else {
                executionPrice = newOrder.getPrice();
            }

            // Execute trade
            executeTrade( newOrder, matchingOrder, tradeSize, executionPrice );

            // Update orders
            updateOrderAfterTrade( newOrder, tradeSize );
            updateOrderAfterTrade( matchingOrder, tradeSize );
        }
    }

    /**
     * Checks if two orders have compatible prices for matching.
     *
     * @param order1 The first order.
     * @param order2 The second order.
     * @return True if prices are compatible, false otherwise.
     */
    private boolean isPriceCompatible( Order order1, Order order2 ) {
        if ( order1.getOrderSide() == OrderSide.BUY ) {
            // BUY order: buy price >= sell price
            return order1.getPrice().compareTo( order2.getPrice() ) >= 0;
        } else {
            // SELL order: sell price <= buy price
            return order1.getPrice().compareTo( order2.getPrice() ) <= 0;
        }
    }


    /**
     * Executes a trade between two compatible orders.
     *
     * @param order1     The first order.
     * @param order2     The second order.
     * @param tradeSize  The quantity to trade.
     * @param tradePrice The price per unit.
     */
    private void executeTrade( Order order1, Order order2, BigDecimal tradeSize, BigDecimal tradePrice ) {
        // Determine buyer and seller
        Order buyOrder = ( order1.getOrderSide() == OrderSide.BUY ) ? order1 : order2;
        Order sellOrder = ( order1.getOrderSide() == OrderSide.SELL ) ? order1 : order2;

        Long buyerId = buyOrder.getCustomerId();
        Long sellerId = sellOrder.getCustomerId();
        String assetName = buyOrder.getAssetName();

        // Total cost of the trade
        BigDecimal totalCost = tradePrice.multiply( tradeSize );

        // Update buyer's TRY asset
        AssetDTO buyerTryAsset = assetService.getAsset( buyerId, "TRY" );
        BigDecimal lockedAmount = buyOrder.getPrice().multiply( tradeSize );
        BigDecimal costDifference = lockedAmount.subtract( totalCost ); // May be positive (refund) or zero

        // Refund any excess locked funds
        buyerTryAsset.setUsableSize( buyerTryAsset.getUsableSize().add( costDifference ) );
        buyerTryAsset.setSize( buyerTryAsset.getSize().subtract( totalCost ) );
        assetService.saveAsset( buyerTryAsset );

        // Update seller's TRY asset
        AssetDTO sellerTryAsset = assetService.getAsset( sellerId, "TRY" );
        sellerTryAsset.setSize( sellerTryAsset.getSize().add( totalCost ) );
        sellerTryAsset.setUsableSize( sellerTryAsset.getUsableSize().add( totalCost ) );
        assetService.saveAsset( sellerTryAsset );

        // Update buyer's asset (assetName)
        AssetDTO buyerAsset = assetService.getAsset( buyerId, assetName );
        if ( buyerAsset == null ) {
            buyerAsset = new AssetDTO();
            buyerAsset.setSize( BigDecimal.ZERO );
            buyerAsset.setUsableSize( BigDecimal.ZERO );
            buyerAsset.setCustomerId( buyerId );
            buyerAsset.setAssetName( assetName );
        }

        buyerAsset.setSize( buyerAsset.getSize().add( tradeSize ) );
        buyerAsset.setUsableSize( buyerAsset.getUsableSize().add( tradeSize ) );
        assetService.saveAsset( buyerAsset );

        // Update seller's asset (assetName)
        AssetDTO sellerAsset = assetService.getAsset( sellerId, assetName );
        // Assets were already locked during order placement (usableSize reduced)
        sellerAsset.setUsableSize( sellerAsset.getSize().subtract( tradeSize ) );
        sellerAsset.setSize( sellerAsset.getSize().subtract( tradeSize ) );
        assetService.saveAsset( sellerAsset );
    }

    /**
     * Updates an order after a trade has been executed.
     *
     * @param order     The order to update.
     * @param tradeSize The quantity that was traded.
     */
    private void updateOrderAfterTrade( Order order, BigDecimal tradeSize ) { // Reduce the remaining size
        BigDecimal remainingSize = order.getSize().subtract( tradeSize );
        if ( order.getOrderSide() == OrderSide.SELL ) {
            order.setSize( remainingSize );
        }
        // Update status if the order is fully matched
        if ( remainingSize.compareTo( BigDecimal.ZERO ) == 0 || remainingSize.compareTo( new BigDecimal( "0.0001" ) ) <= 0 ) {
            order.setStatus( OrderStatus.MATCHED );
            if ( order.getOrderSide() == OrderSide.SELL ) {
                order.setSize( BigDecimal.ZERO );
            }
            // Ensure size is zero 
        } else {
            order.setStatus( OrderStatus.PENDING );
            // Remain pending if not fully matched
        }

        // Save the updated order
        orderRepository.save( order );
    }

    /**
     * Lists orders for a given customer and optional date range.
     *
     * @param customerId The customer ID.
     * @param startDate  The start date (optional).
     * @param endDate    The end date (optional).
     * @return A list of orders.
     */
    @Override
    public List<OrderDTO> listOrders( Long customerId, LocalDateTime startDate, LocalDateTime endDate ) {
        List<Order> orders;
        if ( startDate != null && endDate != null ) {
            orders = orderRepository.findByCustomerIdAndCreateDateBetween( customerId, startDate, endDate );
        } else {
            orders = orderRepository.findByCustomerId( customerId );
        }
        // Map to DTOs
        return orders.stream().map( orderMapper::toDTO ).collect( Collectors.toList() );
    }

    /**
     * Cancels a pending order.
     *
     * @param orderId The ID of the order to cancel.
     */
    public void cancelOrder( Long orderId ) {
        Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new OrderNotFoundException( "Order not found" ) );

        if ( order.getStatus() != OrderStatus.PENDING ) {
            throw new IllegalStateException( "Only pending orders can be canceled." );
        }

        Long customerId = order.getCustomerId();
        String assetName = order.getAssetName();
        BigDecimal remainingOrderSize = order.getSize();
        BigDecimal orderPrice = order.getPrice();

        if ( order.getOrderSide() == OrderSide.BUY ) {
            // Refund locked TRY funds
            BigDecimal lockedAmount = remainingOrderSize.multiply( orderPrice );
            AssetDTO tryAsset = assetService.getAsset( customerId, "TRY" );
            tryAsset.setUsableSize( tryAsset.getUsableSize().add( lockedAmount ) );
            assetService.saveAsset( tryAsset );

        } else if ( order.getOrderSide() == OrderSide.SELL ) {
            // Unlock assets
            AssetDTO asset = assetService.getAsset( customerId, assetName );
            asset.setUsableSize( asset.getUsableSize().add( remainingOrderSize ) );
            assetService.saveAsset( asset );
        }

        // Update order status to CANCELED
        order.setStatus( OrderStatus.CANCELED );
        orderRepository.save( order );
    }
}
