package com.example.smartbilling.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartbilling.entity.Product;
import com.example.smartbilling.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public List<Product> list() { return service.listAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> bySku(@PathVariable String sku) {
        return service.findBySku(sku).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/testpath")
    public String testPath() {
        return System.getProperty("user.dir") + "/uploads/barcodes/";
    }

    @PostMapping
    public Product create(@RequestBody Product p) throws Exception{
        return service.addProduct(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product p) {

        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        p.setId(id);

        try {
            Product updated = service.updateProduct(p);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barcode")
    public ResponseEntity<Product> getByBarcode(@RequestParam String code) {
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Product product = service.findByBarcode(code.trim());
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }
}