package com.example.smartbilling.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smartbilling.entity.Customer;
import com.example.smartbilling.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository repo;
    public CustomerService(CustomerRepository repo) { this.repo = repo; }

    public List<Customer> listAll() { return repo.findAll(); }
    public Customer save(Customer c) { return repo.save(c); }
    public Optional<Customer> findById(Long id) { return repo.findById(id); }
    public void delete(Long id) { repo.deleteById(id); }
}