package com.example.smartbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smartbilling.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> { }