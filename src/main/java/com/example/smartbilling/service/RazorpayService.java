package com.example.smartbilling.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.json.JSONObject;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

    private final RazorpayClient client;

    public RazorpayService(@Value("${razorpay.key}") String key,
                           @Value("${razorpay.secret}") String secret) throws Exception {
        this.client = new RazorpayClient(key, secret);
    }

    public Order createOrder(double amountInRupees, String currency) throws Exception {

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amountInRupees * 100));  
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1);

        return client.orders.create(orderRequest);
    }
}