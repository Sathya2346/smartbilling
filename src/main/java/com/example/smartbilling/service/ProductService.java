package com.example.smartbilling.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smartbilling.entity.Product;
import com.example.smartbilling.repository.ProductRepository;

@Service
@Transactional
public class ProductService {
    private final ProductRepository repo;

    @Autowired
    private BarcodeService barcodeService;

    public ProductService(ProductRepository repo) { this.repo = repo; }

    public List<Product> listAll() { return repo.findAll(); }
    public Product save(Product p) { return repo.save(p); }
    public Optional<Product> findById(Long id) { return repo.findById(id); }
    public Optional<Product> findBySku(String sku) { return repo.findBySku(sku); }

    public Product addProduct(Product product) throws Exception {

        Product savedProduct = repo.save(product);

        String dir = System.getProperty("user.dir") + "/uploads/barcodes/";
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();

        String filePath = dir + savedProduct.getBarcode() + ".png";
        barcodeService.generateBarcodeImage(savedProduct.getBarcode(), filePath);

        return savedProduct;
    }

    public Product updateProduct(Product incoming) throws Exception {
        Product existing = repo.findById(incoming.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update only fields sent from frontend
        existing.setName(incoming.getName());
        existing.setSku(incoming.getSku());
        existing.setSellPrice(incoming.getSellPrice());
        existing.setMrp(incoming.getMrp());
        existing.setGstRate(incoming.getGstRate());
        existing.setStock(incoming.getStock());
        existing.setUnit(incoming.getUnit());
        existing.setStoreDate(incoming.getStoreDate());
        existing.setExpiryDate(incoming.getExpiryDate());
        existing.setDescription(incoming.getDescription());

        // Keep old barcode always
        // DO NOT SET from incoming JSON
        existing.setBarcode(existing.getBarcode());

        Product saved = repo.save(existing);

        // Regenerate barcode image
        String dir = System.getProperty("user.dir") + "/uploads/barcodes/";
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();

        String filePath = dir + saved.getBarcode() + ".png";
        barcodeService.generateBarcodeImage(saved.getBarcode(), filePath);

        return saved;
    }

    public void delete(Long id) {
        Optional<Product> p = repo.findById(id);
        if (p.isPresent()) {
            // Updated line for safer file path handling
            String barcodeFile = System.getProperty("user.dir") 
                    + File.separator + "uploads" 
                    + File.separator + "barcodes" 
                    + File.separator + p.get().getBarcode() + ".png";
            File file = new File(barcodeFile);
            if (file.exists()) file.delete();
            repo.deleteById(id);
        }
    }
    public Product findByBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) return null;

        String cleaned = barcode.trim();
        System.out.println("Searching for barcode: " + cleaned);

        // Try exact match
        Product product = repo.findByBarcodeIgnoreCase(cleaned);
        if (product != null) return product;

        // Try with "SB" prefix if missing
        if (!cleaned.startsWith("SB")) {
            product = repo.findByBarcodeIgnoreCase("SB" + cleaned);
            if (product != null) return product;
        }

        // Try removing "SB" prefix if exists
        if (cleaned.startsWith("SB")) {
            product = repo.findByBarcodeIgnoreCase(cleaned.substring(2));
        }

        return product;
    }
}