package com.example.brokeragefirmapp;

import com.example.brokeragefirmapp.entity.Customer;
import com.example.brokeragefirmapp.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing( auditorAwareRef = "auditorProvider" )
public class BrokerageFirmAppApplication {

    public static void main( String[] args ) {
        SpringApplication.run( BrokerageFirmAppApplication.class, args );
    }

    @Bean
    CommandLineRunner createAdminUser( CustomerRepository customerRepository, PasswordEncoder passwordEncoder ) {
        return args -> {
            if ( customerRepository.findByUsername( "admin" ).isEmpty() ) {
                Customer admin = new Customer();
                admin.setUsername( "admin" );
                admin.setPassword( passwordEncoder.encode( "123" ) );
                admin.setRole( "ADMIN" );
                customerRepository.save( admin );
            }
        };
    }
}
