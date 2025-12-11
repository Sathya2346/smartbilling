package com.example.smartbilling.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "mrp", nullable = false)
    private Double mrp;

    @Column(name = "sell_price", nullable = false)
    private Double sellPrice;

    @Column(name = "gst_rate", nullable = false)
    private Integer gstRate = 18;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false, length = 32)
    private String unit = "pcs";

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String barcode;

    @Column(name = "store_date")
    private LocalDate storeDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @PrePersist
    public void prePersist() {
        // Set timestamps
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Generate barcode if missing
        if (this.barcode == null || this.barcode.isEmpty()) {
            this.barcode = "SB" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }
    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }
    public Integer getGstRate() { return gstRate; }
    public void setGstRate(Integer gstRate) { this.gstRate = gstRate; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public LocalDate getStoreDate() { return storeDate; }
    public void setStoreDate(LocalDate storeDate) { this.storeDate = storeDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}