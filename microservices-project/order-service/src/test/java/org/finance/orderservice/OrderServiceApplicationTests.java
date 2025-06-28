package org.finance.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finance.orderservice.dto.OrderLineItemsDto;
import org.finance.orderservice.dto.OrderRequest;
import org.finance.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
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

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private OrderRepository orderRepository;

	private OrderLineItemsDto getOrderLineItemsDto() {
		return OrderLineItemsDto.builder()
				.skuCode("test sku")
				.price(BigDecimal.ONE)
				.quantity(1)
				.build();
	}

	private OrderRequest getOrderRequest() {
		return OrderRequest.builder()
				.orderLineItemsListDtoList(List.of(getOrderLineItemsDto()))
				.build();
	}

	@Test
	void createOrderSuccess() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestJson = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestJson))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, orderRepository.count());
	}

}
