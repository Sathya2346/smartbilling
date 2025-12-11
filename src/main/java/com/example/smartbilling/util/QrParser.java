package com.example.smartbilling.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses QR content:
 * - SKU: "SKU:<code>"
 * - UPI: standard UPI URI like "upi://pay?pa=...&pn=...&am=...&tn=..."
 */
public class QrParser {

    public static Map<String, String> parse(String content) {
        Map<String, String> result = new HashMap<>();
        if (content == null || content.isEmpty()) return result;

        if (content.startsWith("SKU:")) {
            result.put("type", "SKU");
            result.put("sku", content.substring(4).trim());
            return result;
        }

        if (content.startsWith("upi://pay")) {
            result.put("type", "UPI");
            String[] parts = content.split("\\?");
            if (parts.length > 1) {
                String[] params = parts[1].split("&");
                for (String p : params) {
                    String[] kv = p.split("=");
                    if (kv.length == 2) {
                        result.put(kv[0], kv[1]);
                    }
                }
            }
            return result;
        }

        result.put("type", "TEXT");
        result.put("raw", content);
        return result;
    }
}