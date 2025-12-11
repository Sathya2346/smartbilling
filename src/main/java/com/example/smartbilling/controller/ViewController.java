package com.example.smartbilling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smartbilling.repository.CustomerRepository;
import com.example.smartbilling.repository.InvoiceRepository;
import com.example.smartbilling.repository.ProductRepository;

@Controller
public class ViewController {
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;

    public ViewController(ProductRepository productRepo, CustomerRepository customerRepo, InvoiceRepository invoiceRepo) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.invoiceRepo = invoiceRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("productCount", productRepo.count());
        model.addAttribute("customerCount", customerRepo.count());
        model.addAttribute("invoiceCount", invoiceRepo.count());
        return "index";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerRepo.findAll());
        return "customers";
    }

    @GetMapping("/billing")
    public String billing(Model model) {
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("customers", customerRepo.findAll());
        return "billing";
    }

    @GetMapping("/invoices")
    public String invoices(Model model) {
        model.addAttribute("invoices", invoiceRepo.findAll());
        return "invoices";
    }
    @GetMapping("/invoice-print")
    public String invoicePrint(@RequestParam("no") String invoiceNo, Model model) {
        var inv = invoiceRepo.findByInvoiceNo(invoiceNo)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        model.addAttribute("invoice", inv);
        return "invoice_print";
    }
}