package com.example.smartbilling.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="invoice_id", nullable=false)
    private Invoice invoice;

    @ManyToOne @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @Column(nullable=false, length=64)
    private String sku;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private Integer qty;

    @Column(name="unit_price", nullable=false)
    private Double unitPrice;

    @Column(name="gst_rate", nullable=false)
    private Integer gstRate;

    @Column(name="gst_amount", nullable=false)
    private Double gstAmount;

    @Column(name="line_total", nullable=false)
    private Double lineTotal;

    // Getters and setters...
    public Long getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Integer getGstRate() { return gstRate; }
    public void setGstRate(Integer gstRate) { this.gstRate = gstRate; }
    public Double getGstAmount() { return gstAmount; }
    public void setGstAmount(Double gstAmount) { this.gstAmount = gstAmount; }
    public Double getLineTotal() { return lineTotal; }
    public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
}