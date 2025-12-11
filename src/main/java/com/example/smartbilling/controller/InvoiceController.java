package com.example.smartbilling.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartbilling.dto.InvoiceRequest;
import com.example.smartbilling.dto.InvoiceResponse;
import com.example.smartbilling.entity.Invoice;
import com.example.smartbilling.service.InvoiceService;
import com.example.smartbilling.service.ProductService;
import com.example.smartbilling.service.RazorpayService;
import com.razorpay.Order;
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService service;
    private final ProductService productService;
    private final RazorpayService razorpayService;

    public InvoiceController(InvoiceService service, ProductService productService, RazorpayService razorpayService) {
        this.service = service;
        this.productService = productService;
        this.razorpayService = razorpayService;
    }

    @GetMapping
    public List<InvoiceResponse> list() {
        return service.listAll()
                .stream()
                .map(service::convertToResponse) // map entity to DTO
                .toList();
    }

    @GetMapping("/{invoiceNo}")
    public ResponseEntity<Invoice> get(@PathVariable String invoiceNo) {
        try {
            return ResponseEntity.ok(service.findByInvoiceNo(invoiceNo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public InvoiceResponse create(@RequestBody InvoiceRequest request) {
        return service.createInvoice(request);
    }

    @PostMapping("/razorpay")
    public ResponseEntity<?> createInvoiceRazorpay(@RequestBody InvoiceRequest request) {
        if ("CASH".equalsIgnoreCase(request.getPaymentMode())) {
            return ResponseEntity.badRequest().body("Razorpay is not required for CASH payments");
        }
        try {
            double subtotal = 0;
            double gstTotal = 0;
            for (var item : request.getItems()) {
                var product = productService.findById(item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                double price = item.getUnitPrice() != null ? item.getUnitPrice() : product.getSellPrice();
                int gstRate = product.getGstRate() != null ? product.getGstRate() : 18;
                subtotal += price * item.getQty();
                gstTotal += price * item.getQty() * gstRate / 100;
            }
            double discount = request.getDiscount() != null ? request.getDiscount() : 0;
            double total = subtotal - discount + gstTotal;

            // Create Razorpay order
            Order order = razorpayService.createOrder(total, "INR");

            JSONObject response = new JSONObject();
            response.put("razorpayOrderId", order.get("id").toString());
            response.put("amount", total);
            response.put("currency", "INR");

            return ResponseEntity.ok(response.toMap());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}