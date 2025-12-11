package com.example.smartbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartbilling.entity.InventoryMovement;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> { }