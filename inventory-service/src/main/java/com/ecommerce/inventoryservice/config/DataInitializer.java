package com.ecommerce.inventoryservice.config;

import com.ecommerce.inventoryservice.model.InventoryItem;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(InventoryRepository repository) {
        return args -> {
            // Producto 1 con stock 10
            if (repository.findByProductId("PROD-1").isEmpty()) {
                repository.save(new InventoryItem(UUID.randomUUID().toString(), "PROD-1", 30));
            }
            // Producto 2 sin stock
            if (repository.findByProductId("PROD-2").isEmpty()) {
                repository.save(new InventoryItem(UUID.randomUUID().toString(), "PROD-2", 0));
            }
        };
    }
}30
