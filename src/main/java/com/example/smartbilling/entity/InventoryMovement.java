package com.example.smartbilling.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @Column(name="change_qty", nullable=false)
    private Integer changeQty;

    @Column(nullable=false, length=64)
    private String reason; // SALE, PURCHASE, ADJUSTMENT

    @Column(length=64)
    private String reference;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { createdAt = LocalDateTime.now(); }

    // Getters and setters...
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getChangeQty() { return changeQty; }
    public void setChangeQty(Integer changeQty) { this.changeQty = changeQty; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}