package org.finance.inventoryservice;

import org.finance.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {
    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("inventory_service")
            .withUsername("spring.datasource.username")
            .withPassword("spring.datasource.password");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void getInventoryInStock() throws Exception {
        MvcResult inStock = mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/SKU1"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("true", inStock.getResponse().getContentAsString());
    }

    @Test
    void getInventoryNotInStock() throws Exception {
        MvcResult inStock = mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/iphone_13"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("false", inStock.getResponse().getContentAsString());
    }

}
