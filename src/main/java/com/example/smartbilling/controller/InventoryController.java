package com.example.smartbilling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartbilling.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) { this.service = service; }

    @PostMapping("/adjust/{productId}")
    public ResponseEntity<Void> adjust(@PathVariable Long productId,
                                       @RequestParam int qty,
                                       @RequestParam String reason,
                                       @RequestParam(required = false) String ref) {
        service.adjustStock(productId, qty, reason, ref);
        return ResponseEntity.noContent().build();
    }
}