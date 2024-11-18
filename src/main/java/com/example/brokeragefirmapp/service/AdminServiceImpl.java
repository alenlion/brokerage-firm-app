package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.dto.OrderCreateRequest;
import com.example.brokeragefirmapp.entity.Asset;
import com.example.brokeragefirmapp.entity.Customer;
import com.example.brokeragefirmapp.entity.Order;
import com.example.brokeragefirmapp.enums.OrderSide;
import com.example.brokeragefirmapp.enums.OrderStatus;
import com.example.brokeragefirmapp.exception.InvalidOperationException;
import com.example.brokeragefirmapp.mapper.AssetMapper;
import com.example.brokeragefirmapp.repository.AssetRepository;
import com.example.brokeragefirmapp.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger( AdminServiceImpl.class );

    private final AssetService assetService;
    private final AssetMapper assetMapper;

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public AdminServiceImpl( AssetService assetService, AssetMapper assetMapper, OrderService orderService, OrderRepository orderRepository, AssetRepository assetRepository ) {
        this.assetService = assetService;
        this.assetMapper = assetMapper;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    private static final String[] COMPANY_NAMES = { "Apple", "Microsoft", "Amazon", "Alphabet",
            "Berkshire Hathaway", "Saudi Aramco", "Walmart", "JPMorgan Chase", "Bank of America",
            "Toyota", "ExxonMobil", "HSBC Holdings", "Shell", "China Construction Bank",
            "Volkswagen Group", "UnitedHealth Group", "Agricultural Bank of China", "Chevron",
            "Samsung Electronics" };

    @Override
    public void matchPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus( OrderStatus.PENDING );

        for ( Order order : pendingOrders ) {
            try {
                // Update assets accordingly
                if ( order.getOrderSide() == OrderSide.BUY ) {
                    Asset asset = assetRepository.findByCustomerIdAndAssetName( order.getCustomerId(), order.getAssetName() )
                            .orElseGet( () -> {
                                Asset newAsset = new Asset();
                                newAsset.setCustomerId( order.getCustomerId() );
                                newAsset.setAssetName( order.getAssetName() );
                                newAsset.setSize( BigDecimal.ZERO );
                                newAsset.setUsableSize( BigDecimal.ZERO );
                                return newAsset;
                            } );

                    asset.setSize( asset.getSize().add( order.getSize() ) );
                    asset.setUsableSize( asset.getUsableSize().add( order.getSize() ) );

                    assetRepository.save( asset );

                } else if ( order.getOrderSide() == OrderSide.SELL ) {
                    // Increase TRY asset size and usable size
                    BigDecimal totalAmount = order.getPrice().multiply( order.getSize() );

                    Asset tryAsset = assetRepository.findByCustomerIdAndAssetName( order.getCustomerId(), "TRY" )
                            .orElseGet( () -> {
                                Asset newAsset = new Asset();
                                newAsset.setCustomerId( order.getCustomerId() );
                                newAsset.setAssetName( "TRY" );
                                newAsset.setSize( BigDecimal.ZERO );
                                newAsset.setUsableSize( BigDecimal.ZERO );
                                return newAsset;
                            } );

                    tryAsset.setSize( tryAsset.getSize().add( totalAmount ) );
                    tryAsset.setUsableSize( tryAsset.getUsableSize().add( totalAmount ) );

                    assetRepository.save( tryAsset );
                }

                // Update order status to MATCHED
                order.setStatus( OrderStatus.MATCHED );
                orderRepository.save( order );
                logger.info( "Order matched with ID: {}", order.getId() );

            } catch (Exception ex) {
                logger.error( "Error while matching order ID: {}", order.getId(), ex );
                throw new InvalidOperationException( "Error while matching orders" );
            }
        }
    }

    @Override
    public AssetDTO createAsset( AssetDTO assetDTO ) {
        return assetService.saveAsset( assetDTO );
    }

    @Override
    public void generateRandomAssets( Customer customer ) {
        // Initial asset quantities
        BigDecimal initialQuantity = new BigDecimal( "1000" );
        for ( String companyName : COMPANY_NAMES ) {
            AssetDTO asset = new AssetDTO( customer.getId(), companyName, initialQuantity, initialQuantity );
            try {
                assetService.saveAsset( asset );
                OrderCreateRequest sellOrderRequest = OrderCreateRequest.builder()
                        .customerId( customer.getId() )
                        .assetName( companyName )
                        .orderSide( OrderSide.SELL )
                        .size( initialQuantity )
                        .orderSize( initialQuantity )
                        .price( generateRandomPrice( 10, 100 ) )
                        .build();
                orderService.createOrder( sellOrderRequest );
            } catch (Exception e) {
                logger.error( "Error while creating random assets", e );
            }
        }
    }

    public static BigDecimal generateRandomPrice( int min, int max ) {
        Random random = new Random();
        // Generate a random integer between min and max
        int randomValue = random.nextInt( ( max - min ) + 1 ) + min;
        // Convert to BigDecimal
        return BigDecimal.valueOf( randomValue ).setScale( 0, RoundingMode.DOWN );
    }
}
