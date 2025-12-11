package com.example.smartbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartbilling.entity.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> { }