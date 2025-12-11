package com.example.smartbilling.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smartbilling.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE UPPER(p.barcode) = UPPER(:barcode)")
    Product findByBarcodeIgnoreCase(@Param("barcode") String barcode);

    Optional<Product> findBySku(String sku);
}