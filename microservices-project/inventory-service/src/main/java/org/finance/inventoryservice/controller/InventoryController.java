package org.finance.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import org.finance.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean isInStock(@PathVariable("sku-code") String skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
