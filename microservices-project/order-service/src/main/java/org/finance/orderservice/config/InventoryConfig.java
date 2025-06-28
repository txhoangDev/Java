package org.finance.orderservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class InventoryConfig {
    @Value("http://inventory-service/api/inventory")
    private String inventoryServiceUrl;
}
