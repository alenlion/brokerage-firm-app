package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.entity.Asset;
import com.example.brokeragefirmapp.entity.Order;
import com.example.brokeragefirmapp.enums.OrderSide;
import com.example.brokeragefirmapp.enums.OrderStatus;
import com.example.brokeragefirmapp.exception.ConcurrentModificationException;
import com.example.brokeragefirmapp.exception.InvalidOperationException;
import com.example.brokeragefirmapp.repository.AssetRepository;
import com.example.brokeragefirmapp.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Rayan Aksu
 * @since 11/16/2024
 */

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger( AdminServiceImpl.class);

    private final OrderRepository orderRepository;

    private final AssetRepository assetRepository;

    public AdminServiceImpl( OrderRepository orderRepository, AssetRepository assetRepository ) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public void matchPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus( OrderStatus.PENDING);

        for ( Order order : pendingOrders) {
            try {
                // Update assets accordingly
                if (order.getOrderSide() == OrderSide.BUY) {
                    Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())
                            .orElseGet(() -> {
                                Asset newAsset = new Asset();
                                newAsset.setCustomerId(order.getCustomerId());
                                newAsset.setAssetName(order.getAssetName());
                                newAsset.setSize( BigDecimal.ZERO);
                                newAsset.setUsableSize(BigDecimal.ZERO);
                                return newAsset;
                            });

                    asset.setSize(asset.getSize().add(order.getSize()));
                    asset.setUsableSize(asset.getUsableSize().add(order.getSize()));

                    assetRepository.save(asset);

                } else if (order.getOrderSide() == OrderSide.SELL) {
                    // Increase TRY asset size and usable size
                    BigDecimal totalAmount = order.getPrice().multiply(order.getSize());

                    Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY")
                            .orElseGet(() -> {
                                Asset newAsset = new Asset();
                                newAsset.setCustomerId(order.getCustomerId());
                                newAsset.setAssetName("TRY");
                                newAsset.setSize(BigDecimal.ZERO);
                                newAsset.setUsableSize(BigDecimal.ZERO);
                                return newAsset;
                            });

                    tryAsset.setSize(tryAsset.getSize().add(totalAmount));
                    tryAsset.setUsableSize(tryAsset.getUsableSize().add(totalAmount));

                    assetRepository.save(tryAsset);
                }

                // Update order status to MATCHED
                order.setStatus(OrderStatus.MATCHED);
                orderRepository.save(order);
                logger.info("Order matched with ID: {}", order.getId());

            } catch (OptimisticLockingFailureException ex) {
                logger.error("Optimistic locking failure while matching order ID: {}", order.getId());
                throw new ConcurrentModificationException("Asset was modified by another transaction. Please retry.");
            } catch (Exception ex) {
                logger.error("Error while matching order ID: {}", order.getId(), ex);
                throw new InvalidOperationException("Error while matching orders");
            }
        }
    }
}
