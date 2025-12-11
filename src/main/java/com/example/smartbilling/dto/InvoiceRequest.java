package com.example.smartbilling.dto;

import java.util.List;

public class InvoiceRequest {
    private Long customerId; // nullable for walk-in
    private List<InvoiceItemRequest> items;
    private Double discount;
    private String paymentMode; // CASH, CARD, UPI
    private String upiReference;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<InvoiceItemRequest> getItems() { return items; }
    public void setItems(List<InvoiceItemRequest> items) { this.items = items; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getUpiReference() { return upiReference; }
    public void setUpiReference(String upiReference) { this.upiReference = upiReference; }
}