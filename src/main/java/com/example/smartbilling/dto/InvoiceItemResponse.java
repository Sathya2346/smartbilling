package com.example.smartbilling.dto;

public class InvoiceItemResponse {
    private String sku;
    private String name;
    private Integer qty;
    private Double unitPrice;
    private Integer gstRate;
    private Double gstAmount;
    private Double lineTotal;

    // Getters and Setters
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