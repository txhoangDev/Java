package org.finance.orderservice.client;

import lombok.AllArgsConstructor;
import org.finance.orderservice.config.InventoryConfig;
import org.finance.orderservice.dto.InventoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@AllArgsConstructor
public class InventoryClient {
    private final WebClient webClient;
    private final InventoryConfig inventoryConfig;

    public InventoryResponse[] getInventory(List<String> skuCodes) {
        return webClient.get()
                .uri(inventoryConfig.getInventoryServiceUrl(), uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }

}
