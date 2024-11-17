package com.example.brokeragefirmapp.service;

import com.example.brokeragefirmapp.dto.AssetDTO;
import com.example.brokeragefirmapp.dto.DepositRequestDTO;
import com.example.brokeragefirmapp.dto.WithdrawRequestDTO;
import com.example.brokeragefirmapp.entity.Asset;
import com.example.brokeragefirmapp.entity.TransactionHistory;
import com.example.brokeragefirmapp.enums.TransactionType;
import com.example.brokeragefirmapp.exception.InsufficientBalanceException;
import com.example.brokeragefirmapp.mapper.AssetMapper;
import com.example.brokeragefirmapp.repository.AssetRepository;
import com.example.brokeragefirmapp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rayan Aksu
 * @since 11/15/2024
 */

@Service
@Transactional
public class AssetServiceImpl implements AssetService {

    private static final Logger logger = LoggerFactory.getLogger( AssetService.class );

    private final AssetRepository assetRepository;

    private final TransactionRepository transactionRepository;

    private final AssetMapper assetMapper;

    public AssetServiceImpl( AssetRepository assetRepository, TransactionRepository transactionRepository, AssetMapper assetMapper ) {
        this.assetRepository = assetRepository;
        this.transactionRepository = transactionRepository;
        this.assetMapper = assetMapper;
    }


    @Override
    public List<AssetDTO> listAssets( Long customerId ) {
        List<Asset> assets = assetRepository.findByCustomerId( customerId );
        return assets.stream()
                .map( assetMapper::toDTO )
                .collect( Collectors.toList() );
    }

    @Override
    public void depositMoney( DepositRequestDTO depositRequest ) {
        // Update TRY asset as before
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName( depositRequest.getCustomerId(), "TRY" )
                .orElseGet( () -> new Asset( depositRequest.getCustomerId(), "TRY", BigDecimal.ZERO, BigDecimal.ZERO ) );

        tryAsset.setSize( tryAsset.getSize().add( depositRequest.getAmount() ) );
        tryAsset.setUsableSize( tryAsset.getUsableSize().add( depositRequest.getAmount() ) );

        assetRepository.save( tryAsset );
        logger.info( "Deposited {} TRY for customer ID: {}", depositRequest.getAmount(), depositRequest.getCustomerId() );

        // Save transaction
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setCustomerId( depositRequest.getCustomerId() );
        transactionHistory.setTransactionType( TransactionType.DEPOSIT );
        transactionHistory.setAmount( depositRequest.getAmount() );
        transactionRepository.save( transactionHistory );
    }

    @Override
    public void withdrawMoney( WithdrawRequestDTO withdrawRequest ) {
        // Retrieve TRY asset
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName( withdrawRequest.getCustomerId(), "TRY" ).orElseThrow( () -> new InsufficientBalanceException( "Insufficient TRY balance" ) );
        if ( tryAsset.getUsableSize().compareTo( withdrawRequest.getAmount() ) < 0 ) {
            throw new InsufficientBalanceException( "Insufficient TRY balance" );
        }
        tryAsset.setSize( tryAsset.getSize().subtract( withdrawRequest.getAmount() ) );
        tryAsset.setUsableSize( tryAsset.getUsableSize().subtract( withdrawRequest.getAmount() ) );
        assetRepository.save( tryAsset );
        logger.info( "Withdrew {} TRY for customer ID: {} to IBAN: {}", withdrawRequest.getAmount(), withdrawRequest.getCustomerId(), withdrawRequest.getIban() ); // Record the transaction Transaction transaction = new Transaction(); transaction.setCustomerId(customerId); transaction.setTransactionType(TransactionType.WITHDRAWAL); transaction.setAmount(amount); transaction.setIban(iban); transactionService.recordTransaction(transaction); }

    }

    public AssetDTO getAsset( Long customerId, String assetName ) {
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName( customerId, assetName ).orElse( null );
        return assetMapper.toDTO( tryAsset );
    }
    public Boolean assetIsExist( String assetName ) {
        return assetRepository.countAssetByAssetName( assetName ) != 0;
    }

    @Override
    public AssetDTO saveAsset( AssetDTO asset ) {
        Asset tryAsset =  assetRepository.save( assetMapper.toEntity( asset ) );
        return assetMapper.toDTO( tryAsset );
    }
}
