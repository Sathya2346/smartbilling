package com.example.smartbilling.dto;

import java.util.List;

public class InvoiceResponse {
    private String invoiceNo;
    private Double subtotal;
    private Double gstTotal;
    private Double discount;
    private Double total;
    private String paymentMode;
    private String upiReference;
    private List<InvoiceItemResponse> items;
    private String createdAt;

    // Getters and Setters
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public Double getGstTotal() { return gstTotal; }
    public void setGstTotal(Double gstTotal) { this.gstTotal = gstTotal; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getUpiReference() { return upiReference; }
    public void setUpiReference(String upiReference) { this.upiReference = upiReference; }
    public List<InvoiceItemResponse> getItems() { return items; }
    public void setItems(List<InvoiceItemResponse> items) { this.items = items; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}