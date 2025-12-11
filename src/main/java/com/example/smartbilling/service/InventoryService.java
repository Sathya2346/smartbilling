package com.example.smartbilling.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smartbilling.entity.InventoryMovement;
import com.example.smartbilling.entity.Product;
import com.example.smartbilling.repository.InventoryMovementRepository;
import com.example.smartbilling.repository.ProductRepository;

@Service
@Transactional
public class InventoryService {
    private final ProductRepository productRepo;
    private final InventoryMovementRepository movementRepo;

    public InventoryService(ProductRepository productRepo, InventoryMovementRepository movementRepo) {
        this.productRepo = productRepo;
        this.movementRepo = movementRepo;
    }

    public void adjustStock(Long productId, int changeQty, String reason, String reference) {
        Product p = productRepo.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        p.setStock(p.getStock() + changeQty);
        productRepo.save(p);

        InventoryMovement m = new InventoryMovement();
        m.setProduct(p);
        m.setChangeQty(changeQty);
        m.setReason(reason);
        m.setReference(reference);
        movementRepo.save(m);
    }
}