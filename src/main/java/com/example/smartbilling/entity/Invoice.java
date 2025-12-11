package com.example.smartbilling.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="invoice_no", unique = true, nullable = false)
    private String invoiceNo;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double discount = 0.0;

    @Column(name = "gst_total", nullable = false)
    private Double gstTotal;

    @Column(nullable = false)
    private Double total;

    @Column(name="payment_mode", nullable=false)
    private String paymentMode;

    @Column(name="upi_reference")
    private String upiReference;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters...
    public Long getId() { return id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public Double getGstTotal() { return gstTotal; }
    public void setGstTotal(Double gstTotal) { this.gstTotal = gstTotal; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getUpiReference() { return upiReference; }
    public void setUpiReference(String upiReference) { this.upiReference = upiReference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    
}