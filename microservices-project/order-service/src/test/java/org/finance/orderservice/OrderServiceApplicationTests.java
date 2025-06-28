package org.finance.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finance.orderservice.client.InventoryClient;
import org.finance.orderservice.dto.InventoryResponse;
import org.finance.orderservice.dto.OrderLineItemsDto;
import org.finance.orderservice.dto.OrderRequest;
import org.finance.orderservice.model.Order;
import org.finance.orderservice.model.OrderLineItems;
import org.finance.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
	@MockitoBean
	private InventoryClient inventoryClient;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private OrderRepository orderRepository;

	@Container
	static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("order_service")
			.withUsername("spring.datasource.username")
			.withPassword("spring.datasource.password");

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);
	}

	@BeforeEach
	void setup() {
		orderRepository.deleteAll(); // reset between tests
	}

	private OrderLineItemsDto getOrderLineItemsDto(String skuCode) {
		return OrderLineItemsDto.builder()
				.skuCode(skuCode)
				.price(BigDecimal.ONE)
				.quantity(1)
				.build();
	}

	private OrderRequest getOrderRequest(String skuCode) {
		return OrderRequest.builder()
				.orderLineItemsListDtoList(List.of(getOrderLineItemsDto(skuCode)))
				.build();
	}

	private InventoryResponse[] getInventoryResponseArr() {
        return new InventoryResponse[]{
				new InventoryResponse("test sku", true),
		};
	}

	@Test
	void createOrderSuccess() throws Exception {
		InventoryResponse[] mockInventory = getInventoryResponseArr();

		Mockito.when(inventoryClient.getInventory(Mockito.any())).thenReturn(mockInventory);

		OrderRequest orderRequest = getOrderRequest("test sku");
		String orderRequestJson = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestJson))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, orderRepository.count());
	}

	@Test
	void createOrderFail() throws Exception {
		Mockito.when(inventoryClient.getInventory(Mockito.any())).thenReturn(new InventoryResponse[]{});

		OrderRequest orderRequest = getOrderRequest("fail test sku");
		String orderRequestJson = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestJson))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is5xxServerError());
	}
}
