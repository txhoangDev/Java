package org.finance.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.finance.orderservice.dto.OrderRequest;
import org.finance.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("order placed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is not in stock, please try again later.");
        } catch (IllegalTransactionStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No inventory response found.");
        }
    }
}
