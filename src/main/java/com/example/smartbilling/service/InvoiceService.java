package com.example.smartbilling.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smartbilling.dto.InvoiceItemRequest;
import com.example.smartbilling.dto.InvoiceItemResponse;
import com.example.smartbilling.dto.InvoiceRequest;
import com.example.smartbilling.dto.InvoiceResponse;
import com.example.smartbilling.entity.Customer;
import com.example.smartbilling.entity.Invoice;
import com.example.smartbilling.entity.InvoiceItem;
import com.example.smartbilling.entity.Product;
import com.example.smartbilling.repository.CustomerRepository;
import com.example.smartbilling.repository.InvoiceItemRepository;
import com.example.smartbilling.repository.InvoiceRepository;
import com.example.smartbilling.repository.ProductRepository;

@Service
@Transactional
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;
    private final InvoiceItemRepository itemRepo;
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final InventoryService inventoryService;

    @Value("${app.gst.defaultRate:18}")
    private Integer defaultGstRate;

    public InvoiceService(InvoiceRepository invoiceRepo,
                            InvoiceItemRepository itemRepo,
                            ProductRepository productRepo,
                            CustomerRepository customerRepo,
                            InventoryService inventoryService) {
        this.invoiceRepo = invoiceRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.inventoryService = inventoryService;
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(generateInvoiceNumber());
        invoice.setCustomer(customer);
        invoice.setPaymentMode(request.getPaymentMode());
        invoice.setUpiReference(request.getUpiReference());

        double subtotal = 0.0;
        double gstTotal = 0.0;
        List<InvoiceItem> items = new ArrayList<>();

        for (InvoiceItemRequest ir : request.getItems()) {
            Product product = productRepo.findById(ir.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            int qty = ir.getQty();
            double price = ir.getUnitPrice() != null ? ir.getUnitPrice() : product.getSellPrice();
            int gstRate = product.getGstRate() != null ? product.getGstRate() : defaultGstRate;

            double lineNet = price * qty;
            double gstAmount = (lineNet * gstRate) / 100.0;
            double lineTotal = lineNet + gstAmount;

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setSku(product.getSku());
            item.setName(product.getName());
            item.setQty(qty);
            item.setUnitPrice(price);
            item.setGstRate(gstRate);
            item.setGstAmount(round2(gstAmount));
            item.setLineTotal(round2(lineTotal));
            items.add(item);

            subtotal += lineNet;
            gstTotal += gstAmount;

            inventoryService.adjustStock(product.getId(), -qty, "SALE", invoice.getInvoiceNo());
        }

        double discount = request.getDiscount() != null ? request.getDiscount() : 0.0;
        double total = subtotal - discount + gstTotal;

        invoice.setSubtotal(round2(subtotal));
        invoice.setDiscount(round2(discount));
        invoice.setGstTotal(round2(gstTotal));
        invoice.setTotal(round2(total));
        invoice.setItems(items);

        Invoice saved = invoiceRepo.save(invoice);
        for (InvoiceItem i : items) { itemRepo.save(i); }

        // Map entity to DTO
        InvoiceResponse resp = new InvoiceResponse();
        resp.setInvoiceNo(saved.getInvoiceNo());
        resp.setSubtotal(saved.getSubtotal());
        resp.setDiscount(saved.getDiscount());
        resp.setGstTotal(saved.getGstTotal());
        resp.setTotal(saved.getTotal());
        resp.setPaymentMode(saved.getPaymentMode());
        resp.setUpiReference(saved.getUpiReference());

        List<InvoiceItemResponse> itemsResp = new ArrayList<>();
        for (InvoiceItem i : saved.getItems()) {
            InvoiceItemResponse ir = new InvoiceItemResponse();
            ir.setSku(i.getSku());
            ir.setName(i.getName());
            ir.setQty(i.getQty());
            ir.setUnitPrice(i.getUnitPrice());
            ir.setGstRate(i.getGstRate());
            ir.setGstAmount(i.getGstAmount());
            ir.setLineTotal(i.getLineTotal());
            itemsResp.add(ir);
        }
        resp.setItems(itemsResp);

        return resp;
    }

    public List<Invoice> listAll() { return invoiceRepo.findAll(); }
    public Invoice findByInvoiceNo(String invoiceNo) {
        return invoiceRepo.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
    }

    private String generateInvoiceNumber() {
        long count = invoiceRepo.count() + 1;
        return "INV-" + new DecimalFormat("000000").format(count);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public InvoiceResponse convertToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoiceNo(invoice.getInvoiceNo());
        response.setSubtotal(invoice.getSubtotal());
        response.setGstTotal(invoice.getGstTotal());
        response.setDiscount(invoice.getDiscount());
        response.setTotal(invoice.getTotal());
        response.setPaymentMode(invoice.getPaymentMode());
        response.setUpiReference(invoice.getUpiReference());
        response.setCreatedAt(invoice.getCreatedAt().toString());

        List<InvoiceItemResponse> items = invoice.getItems().stream().map(item -> {
            InvoiceItemResponse ir = new InvoiceItemResponse();
            ir.setSku(item.getSku());
            ir.setName(item.getName());
            ir.setQty(item.getQty());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setGstRate(item.getGstRate());
            ir.setGstAmount(item.getGstAmount());
            ir.setLineTotal(item.getLineTotal());
            return ir;
        }).toList();

        response.setItems(items);
        return response;
    }
}