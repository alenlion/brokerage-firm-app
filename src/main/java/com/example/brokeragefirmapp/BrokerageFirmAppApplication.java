package com.example.brokeragefirmapp;

import com.example.brokeragefirmapp.dto.CustomerDTO;
import com.example.brokeragefirmapp.dto.DepositRequestDTO;
import com.example.brokeragefirmapp.entity.Customer;
import com.example.brokeragefirmapp.mapper.CustomerMapper;
import com.example.brokeragefirmapp.repository.CustomerRepository;
import com.example.brokeragefirmapp.service.AdminService;
import com.example.brokeragefirmapp.service.AssetService;
import com.example.brokeragefirmapp.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing( auditorAwareRef = "auditorProvider" )
public class BrokerageFirmAppApplication {

    public static void main( String[] args ) {
        SpringApplication.run( BrokerageFirmAppApplication.class, args );
    }

    @Bean
    CommandLineRunner initSystem( CustomerMapper customerMapper,CustomerService customerService, AssetService assetService, AdminService adminService, CustomerRepository customerRepository, PasswordEncoder passwordEncoder ) {
        return args -> {
            //Create Admin user
            Optional<Customer> optionalCustomer = customerRepository.findByUsername( "admin" );
            Customer customer = null;
            if( optionalCustomer.isPresent() ) {
                customer = optionalCustomer.get();
            }

            if ( customer == null ) {
                Customer admin = new Customer();
                admin.setUsername( "admin" );
                admin.setPassword( passwordEncoder.encode( "123" ) );
                admin.setRole( "ADMIN" );
                customer = customerRepository.save( admin );
            }
            //Generate Random Assets and amount
            adminService.generateRandomAssets( customer );
            //create TRY for admin user
            DepositRequestDTO depositRequest = new DepositRequestDTO();
            depositRequest.setCustomerId( customer.getId() );
            depositRequest.setAmount( BigDecimal.valueOf( 0 ) );
            assetService.depositMoney( depositRequest );


            //Create 3 user and deposit TRY
            CustomerDTO customerDto = new CustomerDTO();
            customerDto.setUsername( "customer1" );
            customerDto.setPassword( "123" );
            customerDto = customerService.createCustomer( customerDto );
            depositRequest = new DepositRequestDTO();
            depositRequest.setCustomerId( customerDto.getId() );
            depositRequest.setAmount( BigDecimal.valueOf( 1000000 ) );
            assetService.depositMoney( depositRequest );


            customerDto = new CustomerDTO();
            customerDto.setUsername( "customer2" );
            customerDto.setPassword( "123" );
            customerDto = customerService.createCustomer( customerDto );
            depositRequest = new DepositRequestDTO();
            depositRequest.setCustomerId( customerDto.getId() );
            depositRequest.setAmount( BigDecimal.valueOf( 1000000 ) );
            assetService.depositMoney( depositRequest );

            customerDto = new CustomerDTO();
            customerDto.setUsername( "customer3" );
            customerDto.setPassword( "123" );
            customerDto = customerService.createCustomer( customerDto );
            depositRequest = new DepositRequestDTO();
            depositRequest.setCustomerId( customerDto.getId() );
            depositRequest.setAmount( BigDecimal.valueOf( 1000000 ) );
            assetService.depositMoney( depositRequest );
        };
    }

}
