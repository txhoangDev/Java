package org.finance.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.finance.inventoryservice.dto.InventoryResponse;
import org.finance.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> isInStock(@RequestParam List<String> skuCode) {
        return ResponseEntity.status(HttpStatus.OK).body(inventoryService.isInStock(skuCode));
    }
}
