package org.finance.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.finance.orderservice.client.InventoryClient;
import org.finance.orderservice.dto.InventoryResponse;
import org.finance.orderservice.dto.OrderLineItemsDto;
import org.finance.orderservice.dto.OrderRequest;
import org.finance.orderservice.model.Order;
import org.finance.orderservice.model.OrderLineItems;
import org.finance.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsListDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);
        List<String> orderSkuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponsesArr = inventoryClient.getInventory(orderSkuCodes);
        if (inventoryResponsesArr != null && inventoryResponsesArr.length > 0) {
            boolean allProductsInStock = Arrays.stream(inventoryResponsesArr).allMatch(InventoryResponse::getIsInStock);
            if (allProductsInStock) {
                orderRepository.save(order);
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later.");
            }
        } else {
            throw new IllegalTransactionStateException("No inventory response found.");
        }
    }
}
