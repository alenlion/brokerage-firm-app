package com.example.brokeragefirmapp;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.dto.OrderDTO;
import com.example.brokeragefirmapp.entity.Order;
import com.example.brokeragefirmapp.enums.OrderSide;
import com.example.brokeragefirmapp.enums.OrderStatus;
import com.example.brokeragefirmapp.exception.InsufficientBalanceException;
import com.example.brokeragefirmapp.exception.OrderNotFoundException;
import com.example.brokeragefirmapp.exception.ResourceNotFoundException;
import com.example.brokeragefirmapp.mapper.OrderMapper;
import com.example.brokeragefirmapp.repository.OrderRepository;
import com.example.brokeragefirmapp.service.AssetService;
import com.example.brokeragefirmapp.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Rayan Aksu
 * @since 11/18/2024
 */


@Slf4j
@ExtendWith( MockitoExtension.class )
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Order order;

    @BeforeEach
    void setUp() {
        // Initialize OrderDTO
        orderDTO = new OrderDTO();
        orderDTO.setCustomerId( 1L );
        orderDTO.setAssetName( "HP" );
        orderDTO.setSize( new BigDecimal( "10" ) );
        orderDTO.setPrice( new BigDecimal( "50000" ) );
        orderDTO.setOrderSide( OrderSide.BUY );
        orderDTO.setStatus( OrderStatus.PENDING );
        orderDTO.setCreateDate( LocalDateTime.now() );

        // Initialize Order entity
        order = new Order();
        order.setCustomerId( 1L );
        order.setAssetName( "HP" );
        order.setSize( new BigDecimal( "10" ) );
        order.setPrice( new BigDecimal( "50000" ) );
        order.setOrderSide( OrderSide.BUY );
        order.setStatus( OrderStatus.PENDING );
        order.setCreateDate( LocalDateTime.now() );
    }

    /**
     * Test creating an order successfully.
     */
    @Test
    void testCreateOrder_Success() {
        // Arrange
        when( assetService.assetIsExist( orderDTO.getAssetName() ) ).thenReturn( true );
        when( orderMapper.toEntity( any( OrderDTO.class ) ) ).thenReturn( order );
        when( orderRepository.save( any( Order.class ) ) ).thenReturn( order );
        when( orderMapper.toDTO( any( Order.class ) ) ).thenReturn( orderDTO );

        AssetDTO tryAsset = new AssetDTO(1L, "TRY", new BigDecimal( "1000000" ), new BigDecimal( "1000000" ) );
        when( assetService.getAsset( anyLong(), eq( "TRY" ) ) ).thenReturn( tryAsset );

        // Act
        OrderDTO result = orderService.createOrder( orderDTO );

        // Assert
        assertNotNull( result );
        assertEquals( OrderStatus.PENDING, result.getStatus() );
        verify( orderRepository, times( 1 ) ).save( any( Order.class ) );
        verify( assetService, times( 1 ) ).saveAsset( any( AssetDTO.class ) );
    }

    /**
     * Test creating an order when the asset doesn't exist.
     */
    @Test
    void testCreateOrder_AssetNotFound() {
        // Arrange
        when( assetService.assetIsExist( orderDTO.getAssetName() ) ).thenReturn( false );

        // Act & Assert
        assertThrows( ResourceNotFoundException.class, () -> orderService.createOrder( orderDTO ) );
    }

    /**
     * Test creating a BUY order with insufficient TRY balance.
     */
    @Test
    void testCreateOrder_InsufficientBalance_Buy() {
        // Arrange
        when( assetService.assetIsExist( orderDTO.getAssetName() ) ).thenReturn( true );
        AssetDTO tryAsset = new AssetDTO(1L, "TRY", new BigDecimal( "1000" ), new BigDecimal( "1000" ) );
        when( assetService.getAsset( anyLong(), eq( "TRY" ) ) ).thenReturn( tryAsset );

        // Act & Assert
        assertThrows( InsufficientBalanceException.class, () -> orderService.createOrder( orderDTO ) );
    }

    /**
     * Test creating a SELL order with insufficient asset balance.
     */
    @Test
    void testCreateOrder_InsufficientBalance_Sell() {
        // Arrange
        orderDTO.setOrderSide( OrderSide.SELL );
        when( assetService.assetIsExist( orderDTO.getAssetName() ) ).thenReturn( true );
        AssetDTO assetToSell = new AssetDTO(1L, orderDTO.getAssetName(), new BigDecimal( "5" ), new BigDecimal( "5" ) );
        when( assetService.getAsset( anyLong(), eq( orderDTO.getAssetName() ) ) ).thenReturn( assetToSell );

        // Act & Assert
        assertThrows( InsufficientBalanceException.class, () -> orderService.createOrder( orderDTO ) );
    }

    /**
     * Test cancelling an existing pending order.
     */
    @Test
    void testCancelOrder_Success() {
        // Arrange
        when( orderRepository.findById( anyLong() ) ).thenReturn( Optional.of( order ) );
        AssetDTO tryAsset = new AssetDTO(1L, "TRY", new BigDecimal( "10000" ), new BigDecimal( "10000" ) );
        when( assetService.getAsset( anyLong(), eq( "TRY" ) ) ).thenReturn( tryAsset );

        // Act
        orderService.cancelOrder( order.getId() );

        // Assert
        assertEquals( OrderStatus.CANCELED, order.getStatus() );
        verify( orderRepository, times( 1 ) ).save( order );
        verify( assetService, times( 1 ) ).saveAsset( any( AssetDTO.class ) );
    }

    /**
     * Test cancelling an order that is not pending.
     */
    @Test
    void testCancelOrder_NotPending() {
        // Arrange
        order.setStatus( OrderStatus.MATCHED );
        when( orderRepository.findById( anyLong() ) ).thenReturn( Optional.of( order ) );

        // Act & Assert
        assertThrows( IllegalStateException.class, () -> orderService.cancelOrder( order.getId() ) );
    }

    /**
     * Test cancelling a non-existent order.
     */
    @Test
    void testCancelOrder_OrderNotFound() {
        // Arrange
        when( orderRepository.findById( anyLong() ) ).thenReturn( Optional.empty() );

        // Act & Assert
        assertThrows( OrderNotFoundException.class, () -> orderService.cancelOrder( 999L ) );
    }

    /**
     * Test listing orders without date range.
     */
    @Test
    void testListOrders_NoDateRange() {
        // Arrange
        when( orderRepository.findByCustomerId( anyLong() ) ).thenReturn( Collections.singletonList( order ) );
        when( orderMapper.toDTO( any( Order.class ) ) ).thenReturn( orderDTO );

        // Act
        List<OrderDTO> result = orderService.listOrders( 1L, null, null );

        // Assert
        assertNotNull( result );
        assertEquals( 1, result.size() );
    }

    /**
     * Test listing orders within a date range.
     */
    @Test
    void testListOrders_WithDateRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays( 1 );
        LocalDateTime endDate = LocalDateTime.now();
        when( orderRepository.findByCustomerIdAndCreateDateBetween( anyLong(), any( LocalDateTime.class ), any( LocalDateTime.class ) ) )
                .thenReturn( Collections.singletonList( order ) );
        when( orderMapper.toDTO( any( Order.class ) ) ).thenReturn( orderDTO );

        // Act
        List<OrderDTO> result = orderService.listOrders( 1L, startDate, endDate );

        // Assert
        assertNotNull( result );
        assertEquals( 1, result.size() );
    }

    /**
     * Test matching orders with compatible prices.
     */
    @Test
    void testMatchOrders_PriceCompatible() {
        // Arrange
        Order matchingOrder = new Order();
        matchingOrder.setCustomerId( 2L );
        matchingOrder.setAssetName( "HP" );
        matchingOrder.setOrderSide( OrderSide.SELL );
        matchingOrder.setPrice( new BigDecimal( "50000" ) );
        matchingOrder.setSize( new BigDecimal( "10" ) );
        matchingOrder.setStatus( OrderStatus.PENDING );
        matchingOrder.setCreateDate( LocalDateTime.now() );

        AssetDTO buyerTryAsset = new AssetDTO(1L, "TRY", new BigDecimal( "1000000" ), new BigDecimal( "1000000" ) );
        AssetDTO sellerAsset = new AssetDTO(2L, "HP", new BigDecimal( "10" ), new BigDecimal( "10" ) );

        when( orderRepository.findPendingSellOrders( anyString(), any( BigDecimal.class ) ) )
                .thenReturn( Collections.singletonList( matchingOrder ) );
        when( assetService.getAsset( 1L, "TRY" ) ).thenReturn( buyerTryAsset );
        when( assetService.getAsset( 2L, "HP" ) ).thenReturn( sellerAsset );
        when( orderRepository.save( any( Order.class ) ) ).thenReturn( order );

        // Act
        orderService.matchOrders( order );

        // Assert
        assertEquals( OrderStatus.MATCHED, order.getStatus() );
        assertEquals( OrderStatus.MATCHED, matchingOrder.getStatus() );
        verify( orderRepository, times( 2 ) ).save( any( Order.class ) );
        verify( assetService, times( 4 ) ).saveAsset( any( AssetDTO.class ) );
    }

    /**
     * Test matching orders with incompatible prices.
     */
    @Test
    void testMatchOrders_PriceIncompatible() {
        // Arrange
        Order matchingOrder = new Order();
        matchingOrder.setCustomerId( 2L );
        matchingOrder.setAssetName( "HP" );
        matchingOrder.setOrderSide( OrderSide.SELL );
        matchingOrder.setPrice( new BigDecimal( "60000" ) );
        matchingOrder.setSize( new BigDecimal( "10" ) );
        matchingOrder.setStatus( OrderStatus.PENDING );
        matchingOrder.setCreateDate( LocalDateTime.now() );

        when( orderRepository.findPendingSellOrders( anyString(), any( BigDecimal.class ) ) )
                .thenReturn( Collections.singletonList( matchingOrder ) );

        // Act
        orderService.matchOrders( order );

        // Assert
        assertEquals( OrderStatus.PENDING, order.getStatus() );
        verify( orderRepository, times( 1 ) ).save( order );
    }

    /**
     * Test creating an order with null OrderDTO.
     */
    @Test
    void testCreateOrder_NullOrderDTO() {
        // Act & Assert
        assertThrows( NullPointerException.class, () -> orderService.createOrder( ( OrderDTO ) null ) );
    }

    /**
     * Test matching orders when no potential matches are found.
     */
    @Test
    void testMatchOrders_NoPotentialMatches() {
        // Arrange
        when( orderRepository.findPendingSellOrders( anyString(), any( BigDecimal.class ) ) )
                .thenReturn( Collections.emptyList() );

        // Act
        orderService.matchOrders( order );

        // Assert
        assertEquals( OrderStatus.PENDING, order.getStatus() );
    }

    /**
     * Test matching orders when partial matching occurs.
     */
    @Test
    void testMatchOrders_PartialMatch() {
        // Arrange
        Order matchingOrder = new Order();
        matchingOrder.setCustomerId( 2L );
        matchingOrder.setAssetName( "HP" );
        matchingOrder.setOrderSide( OrderSide.SELL );
        matchingOrder.setPrice( new BigDecimal( "50000" ) );
        matchingOrder.setSize( new BigDecimal( "5" ) );
        matchingOrder.setStatus( OrderStatus.PENDING );
        matchingOrder.setCreateDate( LocalDateTime.now() );

        AssetDTO buyerTryAsset = new AssetDTO(1L, "TRY", new BigDecimal( "1000000" ), new BigDecimal( "1000000" ) );
        AssetDTO sellerAsset = new AssetDTO(2L, "HP", new BigDecimal( "5" ), new BigDecimal( "5" ) );

        when( orderRepository.findPendingSellOrders( anyString(), any( BigDecimal.class ) ) )
                .thenReturn( Collections.singletonList( matchingOrder ) );
        when( assetService.getAsset( 1L, "TRY" ) ).thenReturn( buyerTryAsset );
        when( assetService.getAsset( 2L, "HP" ) ).thenReturn( sellerAsset );
        when( orderRepository.save( any( Order.class ) ) ).thenReturn( order );

        // Act
        orderService.matchOrders( order );

        // Assert
        assertEquals( OrderStatus.PENDING, order.getStatus() );
        assertEquals( new BigDecimal( "5" ), order.getSize() );
        assertEquals( OrderStatus.MATCHED, matchingOrder.getStatus() );
    }
}
